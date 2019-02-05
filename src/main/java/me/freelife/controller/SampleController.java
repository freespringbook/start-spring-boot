package me.freelife.controller;

import me.freelife.domain.MemberVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

@Controller
public class SampleController {

    /**
     * 동작 확인하기
     * @param model
     */
    @GetMapping("/sample1")
    public void sample1(Model model) {
        // model.addAttribute("greeting", "Hello World");
        model.addAttribute("greeting", "안녕하세요");
    }

    /**
     * 객체를 화면에 출력하기, HTML 출력하기
     * @param model
     */
    @GetMapping("/sample2")
    public void sample2(Model model) {
        MemberVO vo = new MemberVO(123, "u00", "p00", "홍길동", new Timestamp(System.currentTimeMillis()));
        model.addAttribute("vo", vo);
    }

    /**
     * 리스트를 화면에 출력하기
     * @param model
     */
    @GetMapping("/sample3")
    public void sample3(Model model) {

        List<MemberVO> list = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> {
            list.add(new MemberVO(123, "u0"+i, "p0"+i, "홍길동"+i, new Timestamp(System.currentTimeMillis())));
        });
        model.addAttribute("list", list);
    }

    /**
     * 지역변수의 선언 if~unless 제어 처리
     * @param model
     */
    @GetMapping("/sample4")
    public void sample4(Model model) {

        List<MemberVO> list = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> {
            list.add(new MemberVO(i, "u000"+i%3, "p0000"+i%3, "홍길동"+i, new Timestamp(System.currentTimeMillis())));
        });
        model.addAttribute("list", list);
    }

    /**
     * 인라인 스타일로 Thymeleaf 사용하기
     * @param model
     */
    @GetMapping("/sample5")
    public void sample5(Model model){
        String result = "SUCCESS";
        model.addAttribute("result", result);
    }

    /**
     * 인라인 스타일로 Thymeleaf 사용하기
     * @param model
     */
    @GetMapping("/sample6")
    public void sample6(Model model) {
        List<MemberVO> list = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> {
            list.add(new MemberVO(i, "u0"+i, "p0"+i, "홍길동"+i, new Timestamp(System.currentTimeMillis())));
        });
        model.addAttribute("list", list);

        String result = "SUCCESS";
        model.addAttribute("result", result);
    }

    /**
     * 유틸리티 객체
     */
    @GetMapping("/sample7")
    public void sample7(Model model) {
        model.addAttribute("now", new Date());
        model.addAttribute("price", 123456789);
        model.addAttribute("title", "This is a just sample");
        model.addAttribute("options", Arrays.asList("AAAA","BBB","CCC","DDD"));
    }

    /**
     * Thymeleaf 링크 처리
     */
    @GetMapping("/sample8")
    public void sample8(Model model) {

    }

    /**
     * 페이지에 레이아웃 적용하기
     */
    @GetMapping("/sample/hello")
    public void hello() {

    }
}