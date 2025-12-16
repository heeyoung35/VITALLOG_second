package com.vitallog.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        // 1. ModelMapper 객체 생성
        ModelMapper modelMapper = new ModelMapper();
        /* Setter 메소드 미사용 시 ModelMapper가 Private 필드에 접근 할 수 있도록 설정*/
        // 2. 설정(Configuration) 객체를 가져온다
        modelMapper.getConfiguration()
            // 2-1. private 필드로 직접 접근 해서 매핑
            .setFieldAccessLevel(AccessLevel.PRIVATE)
            // 2-2. 필드 이름을 직접 매핑하도록 허용, 이름이 같은 필드끼리 자동 매핑
            .setFieldMatchingEnabled(true)
            // 2-3. source와 destination의 타입과 필드명이 같을 때만 변환
            .setMatchingStrategy(MatchingStrategies.STRICT);

        //// ★ 핵심: 빈 타입맵을 먼저 생성
        //TypeMap<MenuRequestDTO, Menu> typeMap =
        //    modelMapper.createTypeMap(MenuRequestDTO.class, Menu.class);
        //
        //// ★ 명시적 매핑 설정
        //typeMap.addMappings(mapper -> {
        //    mapper.skip(Menu::setMenuCode);   // PK skip
        //    mapper.skip(Menu::setCategory);   // Category skip
        //});
        //
        //// ★ 암묵적 매핑 적용
        //typeMap.setImplicitMappings(true);

        return modelMapper;
    }
}
