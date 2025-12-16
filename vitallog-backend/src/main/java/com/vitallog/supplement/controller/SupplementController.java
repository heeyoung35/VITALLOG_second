package com.vitallog.supplement.controller;

import com.vitallog.supplement.dto.SupplementRequestDTO;
import com.vitallog.supplement.dto.SupplementResponseDTO;
import com.vitallog.supplement.entity.Supplement;
import com.vitallog.supplement.service.SupplementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Supplement", description = "영양제 관리 관련 API 입니다.")
@RestController
@RequestMapping("/supplement")
@RequiredArgsConstructor
public class SupplementController {

    private final SupplementService supplementService;

    // 영양제 등록
    // http://localhost:8080/supplement
     /*
     {
        "nutMthd": "섭취 방법",
        "nutName": "영양제 명",
        "price": 가격,
        "primaryFnclty": "효능",
        "rawName": "성분",
        "shape": "형태",
        "stock": 수량,
        "storageMthd": "보관 방법",
        "warning": "주의사항"
    }
    */
    @PostMapping
    @Operation(
            summary = "영양제 신규 등록",
            description = "nutNo는 자동 생성되며, 나머지 필드를 모두 입력해야 등록 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public ResponseEntity<SupplementResponseDTO> registSupplement(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "등록할 영양제 정보"
            )
            @Valid @RequestBody SupplementRequestDTO supplementRequestDTO) {

        SupplementResponseDTO newSupplement = supplementService.registSupplement(supplementRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSupplement);
    }

    // 영양제 전체 조회(페이징 처리)
    // http://localhost:8080/supplement
    // // http://localhost:8080/supplement/page={페이지수}&size={몇개씩}
    // 30개 씩 페이징 처리
    @GetMapping
    @Operation(
            summary = "영양제 전체 조회",
            description = "nutNo 기준 오름차순 정렬, 기본 size=30으로 페이징 처리됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public ResponseEntity<Page<SupplementResponseDTO>> findSupplementList(
            @ParameterObject
            @PageableDefault(size = 30, sort = "nutNo") Pageable pageable) {
        Page<SupplementResponseDTO> supplementList = supplementService.findSupplementList(pageable);

        return ResponseEntity.ok(supplementList);
    }

    // 상품 검색(영양제 번호)
    // http://localhost:8080/supplement/{nutNo}
    @GetMapping("/{nutNo}")
    @Operation(summary = "영양제 단일 조회", description = "nutNo(영양제 번호)로 특정 영양제 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 영양제를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public ResponseEntity<SupplementResponseDTO> findSupplementByNutNo(
            @Parameter(description = "검색할 영양제 번호", example = "10")

            @PathVariable Long nutNo) {
        SupplementResponseDTO resultSupplement = supplementService.findSupplementBynutNo(nutNo);

        return ResponseEntity.ok(resultSupplement);
    }

//    // 상품 검색 (영양제 이름에 특정 단어 포함)
//    @GetMapping("/searchNN")
//    public ResponseEntity<?> findSupplementByLike(@RequestParam String nutName) {
//
//        List<Supplement> result = supplementService.findSupplementByNutName(nutName);
//
//        if(result.isEmpty()) {
//            return ResponseEntity.ok("검색 결과가 없습니다. 다시 검색해 주세요~!");
//        }
//        return ResponseEntity.ok(result);
//    }
//
//    // 상품 검색 (영양제 성분에 특정 단어 포함)
//    @GetMapping("/searchRN")
//    public ResponseEntity<?> findSupplementRawNameByLike(@RequestParam String rawName) {
//
//        List<Supplement> result1 = supplementService.findSupplementByRawName(rawName);
//
//        if(result1.isEmpty()) {
//            return ResponseEntity.ok("검색 결과가 없습니다. 다시 검색해 주세요~!");
//        }
//        return ResponseEntity.ok(result1);
//    }
//
//    // 상품 검색 (영양제 효능에 특정 단어 포함)
//    @GetMapping("/searchPF")
//    public ResponseEntity<?> findSupplementPrimaryFncltyeByLike(@RequestParam String primaryFnclty) {
//
//        List<Supplement> result1 = supplementService.findSupplementByPrimaryFnclty(primaryFnclty);
//
//        if(result1.isEmpty()) {
//            return ResponseEntity.ok("검색 결과가 없습니다. 다시 검색해 주세요~!");
//        }
//        return ResponseEntity.ok(result1);
//    }

    // 이름, 성분, 효능 검색 코드를 하나로 통일
    //http://localhost:8080/supplement/search?type={name/raw/fnc/sha/war}&keyword={단어}
    @GetMapping("/search")
    @Operation(
            summary = "특정 단어로 영양제 조회",
            description = "type(name/raw/fnc/sha/war)과 keyword를 이용해 관련 영양제 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "검색 결과 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public ResponseEntity<?> searchSupplement(
            @Parameter(description = "검색할 타입(name, raw, fnc, sha, war)", example = "name")
            @RequestParam String type,
            @Parameter(description = "검색 키워드", example = "비타민")
            @RequestParam String keyword
    ) {
        List<Supplement> result;

        switch (type) {
            case "name":
                result = supplementService.findSupplementByNutName(keyword);
                break;

            case "raw":
                result = supplementService.findSupplementByRawName(keyword);
                break;

            case "fnc":
                result = supplementService.findSupplementByPrimaryFnclty(keyword);
                break;

            case "sha":
                result = supplementService.findSupplementByShape(keyword);
                break;

            case "war":
                result = supplementService.findSupplementByWarring(keyword);
                break;

            default:
                return ResponseEntity.badRequest()
                        .body("검색 타입이 올바르지 않습니다. 사용 가능 타입: name, raw, fnc, sha, war");
        }

        if (result.isEmpty()) {
            return ResponseEntity.ok("검색 결과가 없습니다. 다시 검색해 주세요~!");
        }

        return ResponseEntity.ok(result);
    }

    // 영양제 상세 내용 수정
    // http://localhost:8080/supplement/{nutNo}
    /*
    {
        {"key값"}: {변동값}
    }
    * */
    @PutMapping("/{nutNo}")
    @Operation(summary = "영양제 상세 정보 수정", description = "영양제 상세 정보를 수정하며, 입력되지 않은 필드는 기존 값을 유지합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public ResponseEntity<SupplementResponseDTO> modifySupplement(
            @Parameter(description = "수정할 영양제 번호", example = "5")
            @PathVariable Long nutNo,
            @RequestBody SupplementRequestDTO requestDTO) {
        SupplementResponseDTO updateSupplement = supplementService.modifySupplement(nutNo,requestDTO);

        return ResponseEntity.ok(updateSupplement);
    }

    // 영양제 삭제 기능
    // http://localhost:8080/supplement/{nutNo}
    @DeleteMapping("/{nutNo}")
    @Operation(summary = "영양제 삭제", description = "nutNo를 이용해 영양제 하나를 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public ResponseEntity<String> deleteSupplement(
            @Parameter(description = "삭제할 영양제 번호")
            @PathVariable Long nutNo) {
        String deletedName = supplementService.deleteSupplement(nutNo);

        String message = nutNo + " / " + deletedName + "이(가) 삭제되었습니다.";

        return ResponseEntity.ok(message);
    }
}
