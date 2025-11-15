package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("admin"),
    COACH("coach"),
    PARENT("parent"),
    PLAYER("player");


    private final String spec;

    Role(String spec){
        this.spec = spec;
    }
}
