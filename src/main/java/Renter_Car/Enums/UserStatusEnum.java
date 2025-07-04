package Renter_Car.Enums;

import lombok.Getter;

@Getter
public enum UserStatusEnum {

    PENDING(0),
    ACTIVE(1),
    REPORTED(2),
    LOCKED(3);

    private final int value;

    private UserStatusEnum(int value) {
        this.value = value;
    }

    public static UserStatusEnum fromValue(int value) {
        for (UserStatusEnum status : values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}
