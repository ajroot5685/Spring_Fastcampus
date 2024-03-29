package com.example.project.direction.service

import com.example.project.api.dto.DocumentDto
import com.example.project.api.service.KakaoCategorySearchService
import com.example.project.direction.repository.DirectionRepository
import com.example.project.pharmacy.dto.PharmacyDto;
import com.example.project.pharmacy.service.PharmacySearchService;
import spock.lang.Specification;

class DirectionServiceTest extends Specification {
    private final PharmacySearchService pharmacySearchService = Mock()
    // directionService가 pharmacySearchService를 인자로 필요하기때문에 Mock객체?로 만들어 사용
    // 테스트 시에는 스프링 컨테이너가 실행되지 않는다.
    // 따라서 가짜 데이터를 만들어 주고 mock 객체가 마치 db를 조회하는 것처럼 역할을 수행한다.
    private final DirectionRepository directionRepository = Mock();
    private final KakaoCategorySearchService kakaoCategorySearchService = Mock();
    private final Base62Service base62Service = Mock();

    private DirectionService directionService = new DirectionService(
            pharmacySearchService, directionRepository, kakaoCategorySearchService,base62Service)

    private List<PharmacyDto> pharmacyList;

    def setup(){
        pharmacyList = new ArrayList<>()
        pharmacyList.addAll(
                PharmacyDto.builder()
                        .id(1L)
                        .pharmacyName("돌곶이온누리약국")
                        .pharmacyAddress("주소1")
                        .latitude(37.61040424)
                        .longitude(127.0569046)
                        .build(),
                PharmacyDto.builder()
                        .id(2L)
                        .pharmacyName("호수온누리약국")
                        .pharmacyAddress("주소2")
                        .latitude(37.60894036)
                        .longitude(127.029052)
                        .build()
        )
    }

    def "buildDirectionList - 결과 값이 거리 순으로 정렬이 되는지 확인"() {
        given:
        def addressName = "서울 성북구 종암로10길"
        double inputLatitude = 37.5960650456809
        double inputLongitude = 127.037033003036

        def documentDto = DocumentDto.builder() // DocumentDto에 @Builder 애노테이션 추가하기
                .addressName(addressName)
                .latitude(inputLatitude)
                .longitude(inputLongitude)
                .build()

        when:
        pharmacySearchService.searchPharmacyDtoList() >> pharmacyList
        def results = directionService.buildDirectionList(documentDto)

        then:
        results.size() == 2
        results.get(0).targetPharmacyName == "호수온누리약국"
        results.get(1).targetPharmacyName == "돌곶이온누리약국"
    }

    def "buildDirectionList - 정해진 반경 10km 내에 검색이 되는지 확인"() {
        given:
        // 10km가 넘는 데이터 인위적으로 추가
        pharmacyList.add(
                PharmacyDto.builder()
                        .id(3L)
                        .pharmacyName("경기약국")
                        .pharmacyAddress("주소3")
                        .latitude(37.3825107393401)
                        .longitude(127.236707811313)
                        .build())

        def addressName = "서울 성북구 종암로10길"
        double inputLatitude = 37.5960650456809
        double inputLongitude = 127.037033003036

        // 위와 동일
        def documentDto = DocumentDto.builder() // DocumentDto에 @Builder 애노테이션 추가하기
                .addressName(addressName)
                .latitude(inputLatitude)
                .longitude(inputLongitude)
                .build()

        // 위와 동일
        when:
        pharmacySearchService.searchPharmacyDtoList() >> pharmacyList
        def results = directionService.buildDirectionList(documentDto)

        then:
        results.size() == 2
    }
}