# RCTabPlus

## Overview

RCTabPlus is a fork of the RCTab software at https://github.com/BrightSpots/rcv

This Plus version adds two important vote-counting options that transform the basic RCTab software into a well-designed election method.  The names of these two added options are:

* *Eliminate pairwise losing candidates*

* *Count overvotes when single continuing*


## Installation and running instructions

The RCTab link takes you to a README that includes installation and running instructions.  Note that Linux support for compiling only applies to Ubuntu Linux, and requires *build* adjustments that are automatically done by an IDE (such as IDEA).


## About pairwise losing candidates

The counting rule named *Eliminate pairwise losing candidates* ensures the winner will be a candidate who is supported by more than half the voters.  This result occurs because a candidate who loses every one-on-one contest against every other *continuing* (not-yet-eliminated) candidate is eliminated early.

This early elimination prevents a ballot from getting *stuck* on a candidate who is clearly not popular with other voters.  If too many ballots get *stuck* on the unpopular candidate, those ballots fail to be counted to determine which of the other remaining candidates has the least support.  In other words, the ballot's next-highest ranked candidate can get eliminated before the ballot can be counted as supporting a lower-ranked candidate.

Why does this counting detail matter?  Consider the use of ranked choice voting in Alaska during a congressional election that included a somewhat-popular candidate with lots of name recognition, namely Sarah Palin, who ran as a second Republican candidate.  Alaskan voters who ranked her as their first choice later realized their ballots were not counted when the other Republican candidate was eliminated.  Palin was a pairwise losing candidate during that counting round.  If she had been eliminated as that round's pairwise losing candidate, the election would have been won by the other Republican.  Instead the Democratic candidate won.

The fairness of this concept is easy to understand with a sports metaphor.  When a soccer team loses against every other team still in the playoffs, that pairwise losing soccer team deserves to be eliminated from the tournament.  In a similar way, if a candidate would lose to every other candidate in one-on-one elections, that candidate should not be blocking a voter's ballot from counting for other more-popular candidates.

The bigger picture is that eliminating pairwise losing candidates simplifies voting from the voter's perspective.  Voters should not need to consider how other voters will be voting.  This *tactical voting* becomes unnecessary when this refinement is adopted.  Even better, all voting tactics are defeated, which ensures a savvy voter cannot increase their influence on the result.

Sometimes this issue is called *the spoiler effect*.  Unfortunately this wording implies the pairwise losing candidate should know they are going to *spoil* the election and know enough to drop out.  Instead, what can spoil an election is to assume the candidate who has the fewest supporting ballots is always the least-popular candidate.


## About *counting when single continuing*

The other refinement, the new *overvote rule* named *count when single continuing*, refers to how a ballot is counted when the voter has marked two or more candidates at the same *rank* level of preference.

The previously existing overvote counting rules came from Australia where the paper ballot has a box next to each candidate's name, and the voter writes a *rank* number in each (and, by law, every) box.  It's easy for Australians to write the numbers *1* and *2* and maybe *3* next to their top choices.  Then they fill in the other boxes, often from top to bottom, without using the same number twice.  This system can easily handle 20 candidates.

In the United States, a ranked-choice paper ballot has a grid of ovals arranged in columns and rows.  The columns are labeled something like *rank 1*, *rank 2*, and so on up to *rank 6*.  What happens when there are lots of candidates?  Australia's overvote rules no longer work.  Those primitive rules either pretend that any overvoted column is a mistake and can be ignored, or pretend the ballot deserves to be tossed out when the first overvote is reached.

This new overvote rule named *counting when single continuing* counts the ballot closer to the way a voter would expect.  When an overvote is reached, the ballot becomes inactive during any counting round in which more than one of the overvoted candidates is still continuing.  When just one of the overvoted candidates is continuing, the ballot counts for that continuing candidate.

Why does this counting detail matter?  When Portland (Oregon) first used ranked choice voting to elect a new mayor and city council, each *contest* had about 20 candidates.  Yet the paper ballot had only six *rank* columns.  The two candidates with the most financial support, and the most name recognition, were strongly disliked.  This combination created an unnecessary confusion about how to rank the two strongly disliked candidates lower than most other candidates, yet indicate a preference for one of the disliked candidates as being better than the other.

In other words, this new overvote rule counts every ballot as the voter intended, regardless of what pattern of ovals they marked.  After the many quite-unpopular candidates are eliminated, and just three or four candidates remain, only the marks for those few candidates are used to determine which candidate that ballot supports.


## Further information about these refinements

Here are two graphics and one webpage that further clarify these two refinements.

https://votefair.org/count_overvote_when_single_continuing.png

https://votefair.org/pairwise_counted.png

https://votefair.org/rctab_add_pairwise_counting.html


## Why is ranked choice voting so much better than the primitive *just mark one candidate* method we use now?

This ***plus*** version of ranked choice voting will easily handle an increased number of popular candidates.  Some voters think this characteristic means ranked choice voting should be used in primary elections, or that ranked choice voting will give third-party candidates an opportunity to win a general election.  Although both of these interpretations can be valid, there is a bigger benefit to adopting ranked choice voting.

More importantly, general elections should include the names of a second Republican and a second Democrat.

Using an example with familiar names, this means the 2024 US presidential election could have included Nikki Haley as the second Republican because she received the second-most votes in the Republican primary.  And this means both Joe Biden and Kamala Harris could have been listed as the two Democratic candidates.  Neither Trump nor Biden would have won because more than half the voters would have ranked Haley higher than Trump, and would have ranked Harris higher than Biden.

The long-term result of this wiser use of ranked choice voting will be to reduce the influence of money on elections.  That's because the most effective money-based tactics exploit the limit of just one candidate from each political party.

This money-based effect was clear in the 2008 presidential election.  Some wealthy Republicans gave money to support Barack Obama during the Democratic primary election.  This tactic blocked Hillary Clinton from reaching the general election.  This tactic would have led to the Republican, John McCain, winning the general election if those Republicans had also paid for attack ads against Obama when the general election started.  They thought they didn't need to do this because they assumed Obama could not possibly win the general election.

In short, adopting ranked choice voting for general elections will defeat the ***blocking*** tactics that wealthy campaign contributors use to block reform-minded candidates from reaching our general elections.  And it accomplishes these fairer elections without relying on attempting to limit campaign contributions, which are usually easy to circumvent.

Although most voters correctly understand that money is what controls both the Republican and Democratic parties, few voters realize that monetary influence is easy to reduce simply by adopting ranked choice voting in general elections, and including on the general-election ballot the candidate who gets the second-most votes in each party's primary election.

Then, finally, we the voters will be able to elect problem-solving leaders instead of getting stuck with special-interest puppets.

In preparation for that adoption, it's important to refine ranked choice voting to include the two counting refinements implemented here.  This *ranked choice voting plus* variation overcomes the only two valid criticisms of Australia's version of ranked choice voting, which was designed more than a century ago.
