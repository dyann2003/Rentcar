package Renter_Car.Enums;

import lombok.Getter;

@Getter
public enum SortTypeEnum {
    ASC("ASC"),
    DESC("DESC");

    private final String value;

    private SortTypeEnum(String value) {this.value = value;}
}
