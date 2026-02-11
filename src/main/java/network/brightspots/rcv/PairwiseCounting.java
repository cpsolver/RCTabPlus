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
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.lang.Iterable;
import javafx.util.Pair;
import network.brightspots.rcv.BaseCvrReader.CastVoteRecords;

final class PairwiseCounting {

  public enum PairwiseWinLoseTie {
    WIN,
    LOSE,
    TIE,
    SAME_CANDIDATE
  }

  // Number of candidates for pairwise counting cannot exceed this limit.
  private final int maximumCandidatesForPairwiseCounting = 10;
  // List of names of continuing candidates, but must not exceed above limit.
  private static Array<String> arrayOfCandidateNamesForPairwiseCounting = new Array<>(10);
  // Associate each continuing candidate name with a position in the pairwise counting array.
  private static HashMap<String, Integer> indexForCandidateName = new HashMap<>();
  private static BigDecimal[][] pairwiseCountForFirstOverSecondInPair;
  private static boolean haveCurrentPairwiseCounts = false;

  // Generate list of candidate names for pairwise counting.
  // Output: list based on arrayOfCandidateNamesForPairwiseCounting
  private void generateListOfCandidateNamesForPairwiseCounting() {
    arrayOfCandidateNamesForPairwiseCounting.clear();
    Integer candidateIndex = 0;
    for (String candidate : getCandidateNames()) {
      String candidateName = getNameForCandidate(candidate);
      // Limit to continuing candidates
      if (isCandidateContinuing(candidateName)) {
        // List is empty if continuing candidate count would exceed expected limit.
        if (candidateIndex >= maximumCandidatesForPairwiseCounting) {
          arrayOfCandidateNamesForPairwiseCounting.clear();
          return;
        }
        arrayOfCandidateNamesForPairwiseCounting.add(candidateName);
        // Index numbers start at 1, not 0.
        candidateIndex ++;
        indexForCandidateName.put(candidateName, candidateIndex);
      }
    }
    return;
  }

  // Put zeros into two-dimensional array that will store pairwise counts.
  private void initializePairwiseCounts() {
    int numberOfCandidatesInPairwiseCounting = arrayOfCandidateNamesForPairwiseCounting.size();
    for (int candidateFirstIndex = 1; candidateFirstIndex <=
        numberOfCandidatesInPairwiseCounting; candidateFirstIndex++) {
      for (int candidateSecondIndex = 1;
          candidateSecondIndex <= numberOfCandidatesInPairwiseCounting;
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
    if(comparisonOneIfGreaterMinusIfLess < 0) {
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
    if ((haveCurrentPairwiseCounts) && (getNumberOfWinners() < 2)) {
      return false ;
    }
    // Pairwise counting is not done if there are too many continuing candidates,
    // or if there are only two continuing candidates.
    int numberOfContinuingCandidates = countContinuingCandidates();
    if ((numberOfContinuingCandidates < 3)
    || (numberOfContinuingCandidates > maximumCandidatesForPairwiseCounting)) {
      return false;
    }
    generateListOfCandidateNamesForPairwiseCounting();
    int numberOfCandidatesInPairwiseCounting = arrayOfCandidateNamesForPairwiseCounting.size();
    initializePairwiseCounts();
    int maxRankNumberPlusOne = maxRankingNumber() + 1;
    int currentRound = getCurrentRoundNumber();
    Logger.info("Doing pairwise counting in round: %d", currentRound);
    // Loop through cast vote records.
    for (CastVoteRecord cvr : castVoteRecords) {
      // Ignore cast vote record with no rankings.
      if (numRankings() == 0) {
    	  continue;
      }
      // Get the transfer value for this cast vote record, which can be
      // less than one if election has multiple winners.
      BigDecimal transferValue = cvr.getFractionalTransferValue();
      // Assume same maximum number of continuing candidates as declared at beginning.
      int[] rankingForCandidateIndex = new int[10];
      // Initialize rankings for one ballot, in case any candidates are not ranked.
      for (int candidateIndex = 1;
          candidateIndex <= numberOfCandidatesInPairwiseCounting;
          candidateIndex++) {
        rankingForCandidateIndex[candidateIndex] = maxRankNumberPlusOne;
      }
      // Iterate over all ranks in this cast vote record.
      for (Pair<Integer, CandidatesAtRanking> rankForCandidateName : CandidateRankingsList) {
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
          // Reminder: rank = 1 indicates voter's favorite candidate.
          if (rank < rankingForCandidateIndex[candidateIndex]) {
            rankingForCandidateIndex[candidateIndex] = rank;
          }
        }
      }
      // Add transfer amount to appropriate pairwise count.
      for (int candidateFirstIndex = 1;
          candidateFirstIndex <= numberOfCandidatesInPairwiseCounting;
          candidateFirstIndex++) {
        for (int candidateSecondIndex = 1;
            candidateSecondIndex <= numberOfCandidatesInPairwiseCounting;
            candidateSecondIndex++) {
          int comparisonOneIfGreaterMinusIfLess =
              pairwiseCountForFirstOverSecondInPair[
              candidateFirstIndex][candidateSecondIndex].compareTo(
              pairwiseCountForFirstOverSecondInPair[candidateSecondIndex][candidateFirstIndex]);
          if (comparisonOneIfGreaterMinusIfLess > 0) {
            pairwiseCountForFirstOverSecondInPair[
              candidateFirstIndex][candidateSecondIndex].add(transferValue);
          } else if (comparisonOneIfGreaterMinusIfLess < 0) {
            pairwiseCountForFirstOverSecondInPair[
              candidateSecondIndex][candidateFirstIndex].add(transferValue);
          }
        }
      }
    } // End looping over all ballots.
    haveCurrentPairwiseCounts = true;
    return true;
  }

  // If there is a pairwise losing candidate, return its name.
  // Otherwise return null.
  public String getPairwiseLosingCandidate() {
    String candidateNamePairwiseLosingCandidate = null ;
    boolean encounteredOnlyLosses = false;
    String candidateNameFirstInPair = null;
    String candidateNameSecondInPair = null;
    int numberOfCandidatesInPairwiseCounting = arrayOfCandidateNamesForPairwiseCounting.size();
    // Do outer loop for every candidate in pairwise counts.
    for (int candidateFirstIndex = 1;
        candidateFirstIndex <= numberOfCandidatesInPairwiseCounting;
        candidateFirstIndex++) {
      // Allow for candidate eliminations after pairwise counting was done.
      if (!isCandidateContinuing(arrayOfCandidateNamesForPairwiseCounting[candidateFirstIndex])) {
        continue;
      }
      encounteredOnlyLosses = true;
      // Do inner loop for every continuing candidate.
      for (int candidateSecondIndex = 1;
          candidateSecondIndex <= numberOfCandidatesInPairwiseCounting;
          candidateSecondIndex++) {
        // If first and second candidate in pair are the same, skip this non-pair.
        if (candidateFirstIndex == candidateSecondIndex) {
          continue;
        }
        // Allow for candidate eliminations after pairwise counting was done.
        if (!isCandidateContinuing(arrayOfCandidateNamesForPairwiseCounting[candidateSecondIndex])) {
          continue;
        }
        // Get candidate names based on pairwise index numbers.
        candidateNameFirstInPair = arrayOfCandidateNamesForPairwiseCounting[candidateFirstIndex];
        candidateNameSecondInPair = arrayOfCandidateNamesForPairwiseCounting[candidateSecondIndex];
        // If first-in-pair candidate does not lose,
        // this first-in-pair candidate cannot be pairwise losing candidate.
        if (getWinLoseTieForCandidatePair(candidateNameFirstInPair,
            candidateNameSecondInPair) == PairwiseWinLoseTie.LOSE) {
          encounteredOnlyLosses = false;
          break;
        }
      } // Repeat inner loop through candidates.
      // If candidate lost every pairwise contest, is pairwise losing candidate.
      // There cannot be a second pairwise losing candidate in the same counting round.
      if (encounteredOnlyLosses) {
        return candidateNameFirstInPair;
      }
    } // Repeat outer loop through candidates.
    // If reached here, there is no pairwise losing candidate.
    return null;
  }

  public void logPairwiseCounts() {
    // Determine sequence in which candidates were eliminated.
    Map<String, Integer> candidateToRoundEliminated = getCandidateToRoundEliminated();
    Map<String, Integer> eliminationSequence = candidateToRoundEliminated.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new
        ));
    Logger.info("[INFO] [Begin pairwise counts]");
    // Initialize row and column numbers which are useful for table visualization.
    int pairwiseRow = 1;
    // Loop through candidates in reverse order of elimination.
    // (Might need to invert sequence here)
    for (String candidateNameFirstInPair : eliminationSequence) {
      int candidateFirstIndex = indexForCandidateName.get(candidateNameFirstInPair);
      int pairwiseColumn = 1;
      // (Might need to invert sequence here)
      for (String candidateNameSecondInPair : eliminationSequence) {
        int candidateSecondIndex = indexForCandidateName.get(candidateNameSecondInPair);
        // Log pairwise count for first candidate over second candidate.
        Logger.info(
        "[INFO] [row] %d [column] %d [count] %s [name] %s [versus name] %s",
        pairwiseRow,
        pairwiseColumn,
        pairwiseCountForFirstOverSecondInPair[candidateFirstIndex][candidateSecondIndex].toString(),
        candidateNameFirstInPair,
        candidateNameSecondInPair);
        // Log pairwise count for second candidate over first candidate.
        Logger.info(
        "[INFO] [row] %d [column] %d [count] %s [name] %s [versus name] %s",
        pairwiseColumn,
        pairwiseRow,
        pairwiseCountForFirstOverSecondInPair[candidateSecondIndex][candidateFirstIndex].toString(),
        candidateNameSecondInPair,
        candidateNameFirstInPair);
        pairwiseColumn ++;
      }
      pairwiseRow ++;
    }
    Logger.info("[INFO] [End pairwise counts]");
  }
}