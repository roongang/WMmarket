package com.around.wmmarket.domain.user_role;

import com.around.wmmarket.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_role")
@Entity
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 50)
    private Role role;

    @Builder
    public UserRole(User user,Role role){
        setUser(user);
        this.role=role;
    }
    // getter
    public Integer getId() {
        return id;
    }
    public User getUser() {
        return user;
    }
    public Role getRole() {
        return role;
    }
    // setter
    public void setUser(User user){
        if(this.user!=null) this.user.getUserRoles().remove(this);
        this.user=user;
        if(user!=null) user.getUserRoles().add(this);
    }
    // delete
    @PreRemove
    public void deleteRelation(){
        if(this.user!=null) this.user.getUserRoles().remove(this);
    }
}
