package me.freelife;

import lombok.extern.java.Log;
import me.freelife.domain.PDSBoard;
import me.freelife.domain.PDSFile;
import me.freelife.presistence.PDSBoardRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
@Commit
public class PDSBoardTests {

    @Autowired
    PDSBoardRepository repo;

    @Test
    public void 더미_데이터_생성() {
        PDSBoard pds = new PDSBoard();
        pds.setPname("DOCUMENT 1 - 2");

        PDSFile file1 = new PDSFile();
        file1.setPdsfile("file1.doc");

        PDSFile file2 = new PDSFile();
        file2.setPdsfile("file2.doc");

        pds.setFiles(Arrays.asList(file1, file2));

        log.info("try to save pds");

        repo.save(pds);
    }

    @Test
    public void 조인을_위한_더미_데이터_생성() {
        List<PDSBoard> list = new ArrayList<>();
        IntStream.rangeClosed(1, 100).forEach(i -> {
            PDSBoard pds = new PDSBoard();
            pds.setPname("자료 "+i);

            PDSFile file1 = new PDSFile();
            file1.setPdsfile("file1.doc");

            PDSFile file2 = new PDSFile();
            file2.setPdsfile("file2.doc");

            pds.setFiles(Arrays.asList(file1, file2));
            log.info("try to save pds");
            list.add(pds);
        });
        repo.saveAll(list);
    }

    @Transactional
    @Test
    public void 첨부파일_이름_수정1(){
        Long fno = 1L;
        String newName = "updatedFile1.doc";

        int count = repo.updatePDSFile(fno, newName);
        // @Log 설정된 이후 사용 가능
        log.info("update count: " + count);
    }

    @Transactional
    @Test
    public void 첨부파일_이름_수정2(){
        String newName = "updatedFile2.doc";
        // 반드시 번호가 존재하는지 확인할 것
        Optional<PDSBoard> result = repo.findById(2L);

        result.ifPresent(pds -> {
            log.info("데이터가 존재하므로 update 시도");
            PDSFile target = new PDSFile();
            target.setFno(2L);
            target.setPdsfile(newName);

            //fno 값으로 equals()와 hashcode() 사용
            int idx = pds.getFiles().indexOf(target);

            if(idx > -1){
                List<PDSFile> list = pds.getFiles();
                list.remove(idx);
                list.add(target);
                pds.setFiles(list);
            }
            repo.save(pds);
        });
    }

    @Transactional
    @Test
    public void 첨부파일_삭제() {
        // 첨부 파일 번호
        Long fno = 2L;

        int count = repo.deletePDSFile(fno);
        log.info("DELETE PDSFILE: " + count);
    }

    @Test
    public void 자료와첨부_파일의수_자료번호의_역순출력() {
        repo.getSummary().forEach(arr -> log.info(Arrays.toString(arr)));
    }

}
