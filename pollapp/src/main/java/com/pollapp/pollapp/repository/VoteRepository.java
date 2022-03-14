package com.pollapp.pollapp.repository;

import com.pollapp.pollapp.model.ChoiceVoteCount;
import com.pollapp.pollapp.model.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// using custom query annotation because most of the queries cannot be constructed by JPA dynamic query methods
public interface VoteRepository extends JpaRepository<Vote, Long> {
    // using JPQL constructor expression to return the result in the form of a custom object of class ChoiceVoteCount

    // returns a list of ChoiceVoteCount (1 for every choice in every poll) and initialize every ChoiceVoteCount with choiceId and voteCount
    @Query("SELECT NEW com.pollapp.pollapp.model.ChoiceVoteCount(v.choice.id, count(v.id)) FROM Vote v WHERE v.poll.id in :pollIds GROUP BY v.choice.id")
    List<ChoiceVoteCount> countByPollIdInGroupByChoiceId(@Param("pollIds")List<Long> pollIds);

    // returns a list of ChoiceVoteCount (1 for every choice in the given pollId) and initialize every ChoiceVoteCount with choiceId and voteCount
    @Query("SELECT NEW com.pollapp.pollapp.model.ChoiceVoteCount(v.choice.id, count(v.id)) FROM Vote v WHERE v.poll.id = :pollId GROUP BY v.choice.id")
    List<ChoiceVoteCount> countByPollIdGroupByChoiceId(@Param("pollId") Long pollId);

    // returns a list of Votes casted by userId on polls in pollIds list
    @Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.poll.id IN :pollIds")
    List<Vote> findByUserIdAndPollIdIn(@Param("userId") Long userId, @Param("pollIds") List<Long> pollIds);

    // returns a Vote casted by User with userId on poll with Pollid
    @Query("SELECT v FROM Vote v where v.user.id = :userId and v.poll.id = :pollId")
    Vote findByUserIdAndPollId(@Param("userId") Long userId, @Param("pollId") Long pollId);
}
