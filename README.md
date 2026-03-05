# RCTabPlus

## Overview

RCTabPlus is a fork of the RCTab software at https://github.com/BrightSpots/rcv

This Plus version adds two important vote-counting options that transform the basic RCTab software into a well-designed election method.  The names of these two added options are:

* *Eliminate pairwise losing candidates*

* *Count overvotes when single continuing*


## About pairwise losing candidates

The counting rule named *Eliminate pairwise losing candidates* ensures the winner will be a candidate who is supported by more than half the voters.  This result occurs because a candidate who loses every one-on-one contest against every other *continuing* (not-yet-eliminated) candidate is eliminated early.  This early elimination prevents a ballot from getting *stuck* on a candidate who is clearly not popular with other voters.  If too many ballots get *stuck* on the unpopular candidate, those ballots fail to be counted to determine which of the other remaining candidates has the least support.  In other words, the ballot's next-highest ranked candidate can get eliminated before the ballot can be counted as supporting a lower-ranked candidate.

Why does this counting detail matter?  Consider the use of ranked choice voting in Alaska during a congressional election that included a somewhat-popular candidate with lots of name recognition, namely Sarah Palin, who ran as a second Republican candidate.  Alaskan voters who ranked her as their first choice later realized their ballots were not counted when the other Republican candidate was eliminated.  Palin was a pairwise losing candidate during that counting round.  If she had been eliminated as that round's pairwise losing candidate, the election would have been won by the other Republican.  Instead the Democratic candidate won.

The fairness of this concept is easy to understand with a sports metaphor.  When a soccer team loses against every other team still in the playoffs, that pairwise losing soccer team deserves to be eliminated from the tournament.  In a similar way, if a candidate would lose to every other candidate in one-on-one elections, that candidate should not be blocking a voter's ballot from counting for other more-popular candidates.

The bigger picture is that eliminating pairwise losing candidates simplifies voting from the voter's perspective.  Voters should not need to consider how other voters will be voting.  This *tactical voting* becomes unnecessary when this refinement is adopted.  Even better, all voting tactics are defeated, which ensures a savvy voter cannot increase their influence on the result.

## About *counting when single continuing*

The other refinement, the new *overvote rule* named *count when single continuing*, refers to how a ballot is counted when the voter has marked two or more candidates at the same *rank* level of preference.  Specifically it simply counts a ballot as inactive during any counting round in which more than one of the overvoted candidates is still continuing.  When just one of the overvoted candidates is continuing, the ballot counts for that continuing candidate.

The existing overvote counting options came from Australia where the paper ballot has a box next to each candidate's name.  The voter writes a *rank* number in each (and, by law, every) box.  It's easy for Australians to write the numbers *1* and *2* and maybe *3* next to their top choices.  Then they fill in the other boxes, often from top to bottom, without using the same number twice, regardless of how many candidates are in the contest.  So we should not be following the century-old Australian tradition, which is to either toss out the ballot when the overvote is reached, or skip over the so-called *overvoted* candidates.

Why does this counting detail matter?  When Portland (Oregon) first used ranked choice voting to elect a new mayor and city council, each *contest* had about 20 candidates.  Yet the paper ballot had only six *rank* columns, labeled from *first choice* to *sixth choice*.  Portland voters were taught not to rank more than one candidate in each *rank* column.  Yet the two candidates with the most financial support, and the most name recognition, were strongly disliked.  This combination made it confusing to figure out how to rank these two candidates lower than the other candidates.  If this new overvote rule had been available, every ballot would have been counted as the voter intended, regardless of how they marked their ballot.  Specifically, after the many quite-unpopular candidates had been eliminated, any marking pattern would have been interpreted correctly.


## Links for more details

Here are two graphics and one webpage that further clarify these two refinements.

https://votefair.org/count_overvote_when_single_continuing.png

https://votefair.org/pairwise_counted.png

https://votefair.org/rctab_add_pairwise_counting.html


## Why is ranked choice voting so much better than the primitive *just mark one candidate* method we use now?

This &ldquo;plus&rdquo; version of ranked choice voting will easily handle an increased number of popular candidates.  In fact, US general elections should include the names of two Republicans and two Democrats, not just one nominee from each party.  Of course third-party and independent candidates also will appear on our general-election ballots.

This increased choice will defeat the &ldquo;blocking&rdquo; tactics that wealthy campaign contributors use to block reform-minded candidates from reaching our general elections.  In other words it reduces the influence of money in politics, but without relying on campaign limits that are easy to circumvent.

The 2008 US presidential election serves as a clear example of this blocking tactic.  Some Republicans gave money to help promote Barack Obama over Hillary Clinton during that Democratic primary election, for the purpose of blocking Clinton from reaching the general election.  This tactic worked because only one nominee can come from each party.  If ranked choice voting had bee suddenly adopted, the general election could have had both Obama and Clinton from the Democratic party, and McCain and a second candidate from the Republican party.

In the 2020 presidential election, money was given to Pete Buttigieg and Amy Klobachar to split votes away from Elizabeth Warren and Bernie Sanders so that a less-reform-oriented candidate, Joe Biden, would be easier to defeat during the general election.

In the 2024 presidential election, both Kamala Harris and Joe Biden would have been on the ballot as Democratic candidates, and Nikky Haley would have been the second Republican candidate (because she got the second-most votes during the Republican primary).  Either Harris or Haley would have won because more than half the voters would have ranked Haley higher than Trump, and more than half would have ranked Harris higher than Biden.

Most voters correctly understand that money is what controls both the Republican and Democratic parties.  If ranked choice voting is adopted in general elections, and if the candidates with the second-most votes from each party also are listed on the general-election ballot, the money-based tactics that currently block reform-minded candidates will be easy to defeat.  They we, the voters, will be able to elect problem-solving leaders instead of special-interest puppets.

In the meantime, the criticisms of ranked choice voting will become irrelevant when these two counting refinements are adopted to create a *ranked choice voting plus* variation.
