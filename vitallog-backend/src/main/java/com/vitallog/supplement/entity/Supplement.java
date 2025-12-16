package com.vitallog.supplement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "supplement", schema = "sql5811094")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Supplement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nut_no", nullable = false)
    private Long nutNo;

    @Size(max = 255)
    @Column(name = "nut_name")
    private String nutName;

    @Size(max = 255)
    @Column(name = "shape")
    private String shape;

    @Size(max = 255)
    @NotNull
    @Column(name = "nut_mthd", nullable = false)
    private String nutMthd;

    @NotNull
    @Lob
    @Column(name = "primary_fnclty", nullable = false)
    private String primaryFnclty;

    @Lob
    @Column(name = "warning")
    private String warning;

    @Size(max = 255)
    @Column(name = "storage_mthd")
    private String storageMthd;

    @Lob
    @Column(name = "raw_name")
    private String rawName;

    @ColumnDefault("0")
    @Column(name = "stock")
    private Integer stock;

    @ColumnDefault("0")
    @Column(name = "price")
    private Integer price;


    public Supplement(Long nutNo, int price, int stock) {
        this.nutNo = nutNo;
        this.price = price;
        this.stock = stock;
    }

    // 재고 감소 도메인 로직
    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalArgumentException("재고 부족: 현재=" + stock + " 요청=" + quantity);
        }
        this.stock -= quantity;
    }

    // 재고 증가 도메인 로직
    public void increaseStock(int quantity) {
        this.stock += quantity;
    }

    public void modify(
            String nutName,
            String shape,
            String nutMthd,
            String primaryFnclty,
            String warning,
            String storageMthd,
            String rawName,
            Integer stock,
            Integer price
    ) {
        if (nutName != null) this.nutName = nutName;
        if (shape != null) this.shape = shape;
        if (nutMthd != null) this.nutMthd = nutMthd;
        if (primaryFnclty != null) this.primaryFnclty = primaryFnclty;
        if (warning != null) this.warning = warning;
        if (storageMthd != null) this.storageMthd = storageMthd;
        if (rawName != null) this.rawName = rawName;
        if (stock != null) this.stock = stock;
        if (price != null) this.price = price;
    }

}
