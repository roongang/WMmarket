package com.around.wmmarket.domain.deal_post;

import com.around.wmmarket.domain.BaseTimeEntity;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.deal_success.DealSuccess;
import com.around.wmmarket.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "deal_post")
@Entity
public class DealPost extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime pullingDate;

    @Column(nullable = false)
    private Integer pullingCnt;

    // TODO : dealState 는 입력상태가 정해져 있으므로 ENUM 으로 하는게 맞지 않을까?
    @Column(nullable = false)
    private DealState dealState;

    @OneToMany(mappedBy = "dealPost")
    private List<DealPostImage> dealPostImages = new ArrayList<>();

    @OneToOne(mappedBy = "dealPost")
    private DealSuccess dealSuccess;

    @Builder
    public DealPost(User user,Category category,String title,Integer price,String content,DealState dealState){
        this.user=user;
        this.category=category;
        this.title=title;
        this.price=price;
        this.content=content;
        this.dealState=dealState;
    }
    
    // 영속화전 전처리
    @PrePersist
    public void prePersist(){
        this.pullingCnt=(this.pullingCnt==null)?0:this.pullingCnt;
        this.pullingDate=(this.pullingDate==null)?LocalDateTime.now():this.pullingDate;
    }

    public List<MultipartFile> getMultipartFiles() throws Exception{
        List<MultipartFile> multipartFiles=new ArrayList<>();
        for(DealPostImage dealPostImage:dealPostImages){
            MultipartFile multipartFile=dealPostImage.getMultipartFile();
            multipartFiles.add(multipartFile);
        }
        return multipartFiles;
    }
}
