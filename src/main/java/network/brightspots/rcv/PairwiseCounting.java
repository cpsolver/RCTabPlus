/*
 * RCTab
 * Copyright (c) 2025 Ranked Choice Voting Resource Center.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

/*
 * Purpose: Handles option to eliminate pairwise losing candidates when they occur.
 * Design: Pairwise counting is done just once for a single-winner election.
 * For a multi-winner election, pairwise counting is recalculated to accomodate
 * surplus votes beyond the quota.  Pairwise counting does not start until the
 * specified number of continuing candidates remain.
 * Pairwise counting is done across all precincts and all slices.
 * Inputs: CastVoteRecords for the desired contest, and the number of
 * continuing candidates that start the pairwise counting.
 * Results are logged to console and audit file.
 * Conditions: During tabulation if pairwise counting option requested.
 * Version history: see https://github.com/BrightSpots/rcv.
 */

package network.brightspots.rcv;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.util.Pair;
import network.brightspots.rcv.CandidatesAtRanking;
import network.brightspots.rcv.OutputWriter;


final class PairwiseCounting {

  public enum PairwiseWinLoseTie {
    WIN,
    LOSE,
    TIE,
    SAME_CANDIDATE
  }

  private final Tabulator tabulator;
  private final List<CastVoteRecord> castVoteRecords;
  private final ContestConfig config;

  // Maximum number of continuing candidates to consider for pairwise counting.
  // This number can range from maximum of 7 to minimum of 3.
  private int maximumCandidatesPairwiseCounting = 5;
  // Number of candidates actually included in pairwise counts.
  private int numberOfCandidatesPairwiseCounting;
  // List of names of continuing candidates when pairwise counting first done.
  public ArrayList<String> arrayOfCandidateNamesForPairwiseCounting = new ArrayList<>();
  // Associate each continuing candidate name with a position in the pairwise counting array.
  private HashMap<String, Integer> indexForCandidateName = new HashMap<>();
  private BigDecimal[][] pairwiseCountForFirstOverSecondInPair;
  private boolean haveCurrentPairwiseCounts = false;


  PairwiseCounting(
        Tabulator tabulator, List<CastVoteRecord> castVoteRecords, ContestConfig config) {
    this.tabulator = tabulator;
    this.castVoteRecords = castVoteRecords;
    this.config = config;
    this.pairwiseCountForFirstOverSecondInPair = 
        new BigDecimal[maximumCandidatesPairwiseCounting + 1
        ][maximumCandidatesPairwiseCounting + 1];
  }

  // Generate list of candidate names for pairwise counting.
  // Output: arrayOfCandidateNamesForPairwiseCounting
  private void generateListOfCandidateNamesForPairwiseCounting() {
    arrayOfCandidateNamesForPairwiseCounting.clear();
    indexForCandidateName.clear();
    Integer candidateIndex = 0;
    for (String candidate : config.getCandidateNames()) {
      // Limit to continuing candidates
      if (tabulator.isCandidateContinuing(candidate)) {
        // List is empty if continuing candidate count would exceed expected limit.
        if (candidateIndex >= numberOfCandidatesPairwiseCounting) {
          arrayOfCandidateNamesForPairwiseCounting.clear();
          indexForCandidateName.clear();
          return;
        }
        arrayOfCandidateNamesForPairwiseCounting.add(candidate);
        // Index numbers start at 1, not 0.
        candidateIndex++;
        indexForCandidateName.put(candidate, candidateIndex);
      }
    }
  }

  // Put zeros into two-dimensional array that will store pairwise counts.
  private void initializePairwiseCounts() {
    numberOfCandidatesPairwiseCounting = arrayOfCandidateNamesForPairwiseCounting.size();
    for (int candidateFirstIndex = 1; candidateFirstIndex
        <= numberOfCandidatesPairwiseCounting; candidateFirstIndex++) {
      for (int candidateSecondIndex = 1;
          candidateSecondIndex <= numberOfCandidatesPairwiseCounting;
          candidateSecondIndex++) {
        pairwiseCountForFirstOverSecondInPair[candidateFirstIndex][candidateSecondIndex] =
          BigDecimal.ZERO;
        pairwiseCountForFirstOverSecondInPair[candidateSecondIndex][candidateFirstIndex] =
          BigDecimal.ZERO;
      }
    }
  }

  // Get win or lose or tie result for any one-on-one contest
  // between any two continuing candidates.
  private PairwiseWinLoseTie getWinLoseTieForCandidatePair(
        String candidateNameFirstInPair,
        String candidateNameSecondInPair) {
    int candidateFirstIndex = indexForCandidateName.get(candidateNameFirstInPair);
    int candidateSecondIndex = indexForCandidateName.get(candidateNameSecondInPair);
    if (candidateFirstIndex == candidateSecondIndex) {
      return PairwiseWinLoseTie.SAME_CANDIDATE;
    }
    Integer comparisonOneIfGreaterMinusIfLess =
        pairwiseCountForFirstOverSecondInPair[candidateFirstIndex][candidateSecondIndex].compareTo(
        pairwiseCountForFirstOverSecondInPair[candidateSecondIndex][candidateFirstIndex]);
    if (comparisonOneIfGreaterMinusIfLess < 0) {
      return PairwiseWinLoseTie.LOSE;
    } else if (comparisonOneIfGreaterMinusIfLess > 0) {
      return PairwiseWinLoseTie.WIN;
    }
    return PairwiseWinLoseTie.TIE;
  }

  // When needed, do the pairwise counting among the continuing candidates.
  // Each combination of two different candidates is counted according to:
  // * How many ballots rank the first candidate higher than the second candidate.
  // * How many ballots rank the second candidate higher than the first candidate.
  // The number of ballots that rank both candidates the same is not needed,
  // but can be calculated by subtracting both pairwise counts from the total number of
  // ballots (including exhausted ballots).
  // Returns: True if pairwise counting done, false if not done.
  public boolean doPairwiseCounting() {
    // Pairwise counts do not change if only one candidate can win.
    if ((haveCurrentPairwiseCounts) && (config.getNumberOfWinners() < 2)) {
      return false;
    }

    // For now, specify numberOfCandidatesPairwiseCounting.
    // Later, get number from config info.
    numberOfCandidatesPairwiseCounting = 5;

    // Pairwise counting is not done if there are too many continuing candidates.
    if ((tabulator.countContinuingCandidates() > numberOfCandidatesPairwiseCounting)
        || (numberOfCandidatesPairwiseCounting > maximumCandidatesPairwiseCounting)) {
      return false;
    }
    generateListOfCandidateNamesForPairwiseCounting();
    numberOfCandidatesPairwiseCounting = arrayOfCandidateNamesForPairwiseCounting.size();
    // Also not done if only one or two candidate names found.
    if (numberOfCandidatesPairwiseCounting < 3) {
      return false;
    }
    initializePairwiseCounts();
    int currentRound = tabulator.getCurrentRoundNumber();
    Logger.info("Doing pairwise counting in round %d", currentRound);
    // Loop through cast vote records.
    for (CastVoteRecord cvr : castVoteRecords) {
      // Ignore cast vote record with no rankings.
      if (cvr.candidateRankings.numRankings() == 0) {
        continue;
      }
      int maxRankNumberPlusOne = cvr.candidateRankings.maxRankingNumber() + 1;
      // Get the transfer value for this cast vote record, which can be
      // less than one if election has multiple winners.
      BigDecimal transferValue = cvr.getFractionalTransferValue();
      // Assume number of continuing candidates does not exceed limit declared at beginning.
      int[] rankingForCandidateIndex = new int[numberOfCandidatesPairwiseCounting + 1];
      // Initialize rankings for one ballot, in case any candidates are not ranked.
      for (int candidateIndex = 1;
          candidateIndex <= numberOfCandidatesPairwiseCounting;
          candidateIndex++) {
        rankingForCandidateIndex[candidateIndex] = maxRankNumberPlusOne;
      }
      // Handling next ballot.
      // Iterate over all ranks in this cast vote record.
      for (Pair<Integer, CandidatesAtRanking> rankForCandidateName : cvr.candidateRankings) {
        Integer rank = rankForCandidateName.getKey();
        CandidatesAtRanking candidatesAtRanking = rankForCandidateName.getValue();
        // Ignore rankings that have no candidates.
        if (candidatesAtRanking == null) {
          continue;
        }
        // Allow for same candidate to be ranked more than once.
        // Iterate over candidates that have this ranking.
        for (String candidateName : candidatesAtRanking) {
          Integer candidateIndex = indexForCandidateName.get(candidateName);
          if (candidateIndex == null) {
            continue;
          }
          // Reminder: rank = 1 indicates voter's favorite candidate.
          if (rank < rankingForCandidateIndex[candidateIndex]) {
            rankingForCandidateIndex[candidateIndex] = rank;
          }
        }
      }
      // Add transfer amount to appropriate pairwise count.
      for (int candidateFirstIndex = 1;
          candidateFirstIndex <= numberOfCandidatesPairwiseCounting;
          candidateFirstIndex++) {
        for (int candidateSecondIndex = candidateFirstIndex + 1;
            candidateSecondIndex <= numberOfCandidatesPairwiseCounting;
            candidateSecondIndex++) {
          // Compare rankings, lower rank number is higher ranking.
          int comparisonOneIfGreaterMinusIfLess =
              Integer.compare(
                  rankingForCandidateIndex[candidateFirstIndex],
                  rankingForCandidateIndex[candidateSecondIndex]);
          if (comparisonOneIfGreaterMinusIfLess < 0) {
            pairwiseCountForFirstOverSecondInPair[
                candidateFirstIndex][candidateSecondIndex] = 
                pairwiseCountForFirstOverSecondInPair[
                candidateFirstIndex][candidateSecondIndex].add(transferValue);
            // Logging if needed:
            // Logger.info(
            //     "%s over %s at weight %s",
            //     arrayOfCandidateNamesForPairwiseCounting.get(candidateFirstIndex - 1),
            //     arrayOfCandidateNamesForPairwiseCounting.get(candidateSecondIndex - 1),
            //     transferValue);
          } else if (comparisonOneIfGreaterMinusIfLess > 0) {
            pairwiseCountForFirstOverSecondInPair[
                candidateSecondIndex][candidateFirstIndex] = 
                pairwiseCountForFirstOverSecondInPair[
                candidateSecondIndex][candidateFirstIndex].add(transferValue);
            // Logging if needed:
            // Logger.info(
            //     "%s over %s at weight %s",
            //     arrayOfCandidateNamesForPairwiseCounting.get(candidateSecondIndex - 1),
            //     arrayOfCandidateNamesForPairwiseCounting.get(candidateFirstIndex - 1),
            //     transferValue);
          }
          // If equal, no pairwise preference.
        }
      }
    } // End looping over all ballots.
    haveCurrentPairwiseCounts = true;
    return haveCurrentPairwiseCounts;
  }

  // If there is a pairwise losing candidate, return its name.
  // Otherwise return null.
  public String getPairwiseLosingCandidate() {
    boolean pairwiseCountingDone = doPairwiseCounting();
    String candidateNamePairwiseLosingCandidate = null;
    if (!pairwiseCountingDone
        || (numberOfCandidatesPairwiseCounting > maximumCandidatesPairwiseCounting)) {
      return candidateNamePairwiseLosingCandidate;
    }
    boolean encounteredOnlyLosses = false;
    String candidateNameFirstInPair = null;
    String candidateNameSecondInPair = null;
    numberOfCandidatesPairwiseCounting = arrayOfCandidateNamesForPairwiseCounting.size();
    // Do outer loop for every candidate in pairwise counts.
    for (int candidateFirstIndex = 1;
        candidateFirstIndex <= numberOfCandidatesPairwiseCounting;
        candidateFirstIndex++) {
      candidateNameFirstInPair = 
          arrayOfCandidateNamesForPairwiseCounting.get(candidateFirstIndex - 1);
      // Allow for candidate eliminations after pairwise counting was done.
      if (!tabulator.isCandidateContinuing(candidateNameFirstInPair)) {
        continue;
      }
      encounteredOnlyLosses = true;
      // Do inner loop for every continuing candidate.
      for (int candidateSecondIndex = 1;
          candidateSecondIndex <= numberOfCandidatesPairwiseCounting;
          candidateSecondIndex++) {
        // If first and second candidate in pair are the same, skip this non-pair.
        if (candidateFirstIndex == candidateSecondIndex) {
          continue;
        }
        candidateNameSecondInPair = arrayOfCandidateNamesForPairwiseCounting.get(
            candidateSecondIndex - 1);
        // Allow for candidate eliminations after pairwise counting was done.
        if (!tabulator.isCandidateContinuing(candidateNameSecondInPair)) {
          continue;
        }
        // If first-in-pair candidate does not lose,
        // this first-in-pair candidate cannot be pairwise losing candidate.
        if (getWinLoseTieForCandidatePair(candidateNameFirstInPair,
            candidateNameSecondInPair) != PairwiseWinLoseTie.LOSE) {
          encounteredOnlyLosses = false;
          break;
        }
      } // Repeat inner loop through candidates.
      // If candidate lost every pairwise contest, is pairwise losing candidate.
      // There cannot be a second pairwise losing candidate in the same counting round.
      if (encounteredOnlyLosses) {
        candidateNamePairwiseLosingCandidate = candidateNameFirstInPair;
        return candidateNamePairwiseLosingCandidate;
      }
    } // Repeat outer loop through candidates.
    // If reached here, there is no pairwise losing candidate.
    return candidateNamePairwiseLosingCandidate;
  }

  // For reporting purposes only, get the pairwise count between any two candidates.
  // Returns minus one if the two candidate names are the same,
  // or if either candidate name is not in the pairwise counts,
  public BigDecimal getPairwiseCountForCandidatePair(
        String candidateNameFirstInPair,
        String candidateNameSecondInPair) {
    if (candidateNameFirstInPair.equals(candidateNameSecondInPair)) {
      return BigDecimal.ZERO.subtract(BigDecimal.ONE);
    }
    if ((!indexForCandidateName.containsKey(candidateNameFirstInPair))
        || (!indexForCandidateName.containsKey(candidateNameSecondInPair))) {
      return BigDecimal.ZERO.subtract(BigDecimal.ONE);
    }
    int candidateFirstIndex = indexForCandidateName.get(candidateNameFirstInPair);
    int candidateSecondIndex = indexForCandidateName.get(candidateNameSecondInPair);
    return pairwiseCountForFirstOverSecondInPair[candidateFirstIndex][candidateSecondIndex];
  }
}
