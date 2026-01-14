package cz.reservation.entity;

import cz.reservation.constant.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    private String telephoneNumber;

    @Column
    private String password;

    @Column
    private String fullName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "users"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerEntity> players = new ArrayList<>();

    @Column
    private LocalDateTime createdAt;


    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private CoachEntity coach;

    public UserEntity(Builder builder){
        this.email = builder.email;
        this.telephoneNumber = builder.telephoneNumber;
        this.password = builder.password;
        this.fullName = builder.fullName;
        this.roles = builder.roles;
        this.createdAt = builder.createdAt;

    }

    public static Builder builder(){
        return new Builder(){

        };
    }

    public static class Builder{

        private String email;
        private String telephoneNumber;
        private String password;
        private String fullName;
        private Set<Role> roles;
        private LocalDateTime createdAt;

        private Builder(){

        }

        public Builder email(String email){
            this.email = email;
            return this;
        }

        public Builder telephoneNumber(String telephoneNumber){
            this.telephoneNumber = telephoneNumber;
            return this;
        }

        public Builder password(String password){
            this.password = password;
            return this;
        }

        public Builder fullName(String fullName ){
            this.fullName = fullName;
            return this;
        }

        public Builder roles(Set<Role> roles){
            this.roles = roles;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt){
            this.createdAt = createdAt;
            return this;
        }


        public UserEntity build(){
            return new UserEntity(this);
        }
    }

}
