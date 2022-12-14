package com.example.sheetmusiclist.controller.review;


import com.example.sheetmusiclist.dto.review.ReviewCreateRequestDto;
import com.example.sheetmusiclist.dto.review.ReviewDeleteRequestDto;
import com.example.sheetmusiclist.dto.review.ReviewEditRequestDto;
import com.example.sheetmusiclist.dto.review.ReviewFindRequestDto;
import com.example.sheetmusiclist.entity.member.Member;
import com.example.sheetmusiclist.exception.MemberNotFoundException;
import com.example.sheetmusiclist.repository.member.MemberRepository;
import com.example.sheetmusiclist.response.Response;
import com.example.sheetmusiclist.service.review.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
@Api(value = "Review Controller", tags = "Review")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;
    private final MemberRepository memberRepository;

    // 리뷰 작성
    @ApiOperation(value = "리뷰 작성", notes = "리뷰를 작성한다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/reviews")
    public Response createReview(@Valid @RequestBody ReviewCreateRequestDto req){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);

        reviewService.createReview(member,req);
        return Response.success("리뷰 작성 완료");
    }

    // 리뷰 전체 조회(by 악보)
    @ApiOperation(value = "해당 악보의 리뷰 전체 조회", notes = "해당 악보의 리뷰를 전체 조회한다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/music/{id}/reviews")
    public Response getReview(@PageableDefault(size = 10,sort = "id",direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable ("id") Long id){

        return Response.success(reviewService.findReviews(pageable,id));
    }
    // 리뷰 수정
    @ApiOperation(value = "리뷰 수정", notes = "리뷰를 수정한다.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/reviews/{id}")
    public Response editReview(@ApiParam(value = "리뷰 id",required = true)
            @PathVariable("id") Long id, @Valid @RequestBody ReviewEditRequestDto req){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);

        reviewService.editReview(member, id, req);
        return Response.success("리뷰 수정 완료");
    }

    //리뷰 삭제
    @ApiOperation(value = "리뷰 삭제", notes = "리뷰를 삭제한다.")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/reviews/{id}")
    public Response deleteReview(@ApiParam(value = "리뷰 id",required = true)@PathVariable("id") Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        reviewService.deleteReview(id, member);
        return Response.success("삭제 완료");
    }
}
