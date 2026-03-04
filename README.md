# RCTabPlus

## Overview

RCTabPlus is a fork of the RCTab software at https://github.com/BrightSpots/rcv

<<<<<<< HEAD
This Plus version adds a new Overvote Rule named "Count overvote when single continuing".  This overvote-counting rule much better matches the clear intent of a voter in the United States where a ranked choice ballot typically has no more than 6 "rank" columns of ovals, even when there are as many as 20 or 30 candidates.

The RCTab (non-plus) software only supports overvote-counting rules that make sense in Australia where a voter writes a number in a box next to each candidate's name.  In that case a voter can easily write numbers that are as large as the number of candidates, and can easily avoid an overvote by not writing the same number twice.

The overvote rule named "count overvote when single continuing" simply counts a ballot as inactive during any counting round in which more than one of the overvoted candidates is still continuing.  When just one of the overvoted candidates is continuing, the ballot counts for that continuing candidate.

The following graphic summarizes how this overvote rule works, and why it is needed:

https://votefair.org/count_overvote_when_single_continuing.png
=======
This software branch adds a new counting rule named "Eliminate pairwise losing candidates".  This counting rule virtually always ensures the winner will be a candidate who is supported by more than half the voters.

How does this counting rule work?  During the final counting rounds a process named pairwise counting is done to determine whether any of the continuing (not-yet-eliminated) candidates would lose every one-on-one contest against every other continuing candidate.  If there is, this pairwise losing candidate is eliminated, even if a different candidate received the fewest transferred votes during that counting round.

The fairness of this concept is easy to understand with a sports metaphor.  When a soccer team loses against every other team still in the playoffs, that pairwise losing soccer team deserves to be eliminated from the tournament.

Eliminating pairwise losing candidates will empower ranked choice voting to overcome opposition from voters who criticize ranked choice voting's &ldquo;center squeeze effect&rduo; and its ocassional failures to elect the &ldquo;Condorcet winner.&rdquo;

Here are some additional details about eliminting pairwise losing candidates:

* When ranked choice voting has been used in general elections in the United States, it has sometimes elected the wrong candidate.  In the 2022 Alaska congressional special election Sarah Palin was a pairwise losing candidate.  In the 2009 Burlington mayoral election Kurt Wright was a pairwise losing candidate.  If these pairwise losing candidates had been eliminated, the candidate from the opposite political party would have won with support from more than half the voters.  Instead, during the top-three round, lots of ballots were basically ignored because they were &ldquo;stuck&rdquo; getting counted for the pairwise losing candidate.

* This counting option works for both single-winner and multi-winner (proportional) ranked choice voting.

* This &ldquo;plus&rdquo; version of ranked choice voting will easily handle an increased number of popular candidates.  In fact, US general elections should include the names of two Republicans and two Democrats, not just one nominee from each party.  Of course third-party and independent candidates also will appear on our general-election ballots.

* This increased choice will defeat the &ldquo;blocking&rdquo; tactics that wealthy campaign contributors use to block reform-minded candidates from reaching our general elections.  In other words it reduces the influence of money in politics, but without relying on campaign limits that are easy to circumvent.

* When this counting option is chosen, the overvote rule named &ldquo;count when single continuing&rdquo; also should be chosen.

* Together, these two counting refinements will enable a voter to rank candidates in the voter's honest order of preference, without concern about how other voters will rank the candidates.

* This branch of RCTabPlus does not implement the &ldquo;count when single continuing&rdquo; overvote rule.  That's because these two branches are waiting to be merged into the non-plus version of the RCTab software, and these two merges must be done separately.

The following graphic summarizes how this new counting rule &mdash; eliminate pairwise losing candidates &mdash; works, and why it's needed:

https://votefair.org/pairwise_counted.png

Additional details about this RCTabPlus improvement are explained here:

https://votefair.org/rctab_add_pairwise_counting.html
>>>>>>> feature_issue_984_eliminate_pairwise_losing_candidates
