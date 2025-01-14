package com.project.roomescape.integration;

import com.project.roomescape.model.Comment;
import com.project.roomescape.repository.CommentRepository;
import com.project.roomescape.requestDto.CommentRequestDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.config.location=classpath:application-test.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @Order(1)
    @DisplayName("코멘트 입력하기")
    @Transactional
    void createComment_OneComment_CreateOneComment() {
        CommentRequestDto commentRequestDto = new CommentRequestDto("임시 코멘트");

        webTestClient.post().uri("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(commentRequestDto)
                .exchange()
                .expectStatus().isOk();

        //실제로 db에서 코멘트를 찾아옵니다.
        List<Comment> commentList = commentRepository.findAll();

        //실제 찾아온 코멘트의 내용이 내가 post로 보낸 body와 일치하는지 확인합니다.
        assertEquals(commentList.size(), 1);
        assertEquals(commentList.get(0).getComment(), "임시 코멘트");
    }

}
