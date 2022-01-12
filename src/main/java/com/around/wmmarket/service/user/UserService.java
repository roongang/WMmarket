package com.around.wmmarket.service.user;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.FileHandler;
import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.dealPost.DealPostGetResponseDto;
import com.around.wmmarket.controller.dto.user.UserGetResponseDto;
import com.around.wmmarket.controller.dto.user.UserSaveRequestDto;
import com.around.wmmarket.controller.dto.user.UserSaveResponseDto;
import com.around.wmmarket.controller.dto.user.UserUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.domain.user_like.UserLike;
import com.around.wmmarket.service.dealPost.DealPostService;
import com.around.wmmarket.service.userLike.UserLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService{
    private final UserRepository userRepository;
    private final DealPostRepository dealPostRepository;

    private final UserLikeService userLikeService;
    private final DealPostService dealPostService;

    private final PasswordEncoder passwordEncoder;
    private final FileHandler fileHandler;

    @Transactional
    public UserSaveResponseDto save(UserSaveRequestDto requestDto){
        // check duplicate user
        if(userRepository.findByEmail(requestDto.getEmail()).isPresent()) throw new CustomException(ErrorCode.DUPLICATE_USER_EMAIL);

        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .role(Role.valueOf(requestDto.getRole()))
                .city_1(requestDto.getCity_1())
                .town_1(requestDto.getTown_1())
                .city_2(requestDto.getCity_2())
                .town_2(requestDto.getTown_2())
                .build();
        if(requestDto.getImage()!=null) user.setImage(fileHandler.parseUserImage(requestDto.getImage()));
        return new UserSaveResponseDto(userRepository.save(user).getId());
    }

    public UserGetResponseDto getUserDto(String email){
        User user = userRepository.findByEmail(email)
                .orElse(null);
        if(user==null) return null;
        return UserGetResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .city_1(user.getCity_1())
                .town_1(user.getTown_1())
                .city_2(user.getCity_2())
                .town_2(user.getTown_2())
                .isAuth(user.getIsAuth())
                .code(user.getCode())
                .build();
    }
    public UserGetResponseDto getUserDto(Integer id){
        User user = userRepository.findById(id)
                .orElse(null);
        if(user==null) return null;
        return UserGetResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .city_1(user.getCity_1())
                .town_1(user.getTown_1())
                .city_2(user.getCity_2())
                .town_2(user.getTown_2())
                .isAuth(user.getIsAuth())
                .code(user.getCode())
                .build();
    }

    public User getUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
    }
    public User getUser(Integer userId){
        return userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public String getUserEmail(int userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        return user.getEmail();
    }

    @Transactional
    public void update(SignedUser signedUser,Integer id, UserUpdateRequestDto requestDto) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        // compare id, signed user
        User user=userRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if(!user.getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);

        // update logic
        if(requestDto.getPassword()!=null) user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        if(requestDto.getNickname()!=null) user.setNickname(requestDto.getNickname());
        if(requestDto.getRole()!=null) user.setRole(Role.valueOf(requestDto.getRole()));
        if(requestDto.getCity_1()!=null) user.setCity_1(requestDto.getCity_1());
        if(requestDto.getTown_1()!=null) user.setTown_1(requestDto.getTown_1());
        if(requestDto.getCity_2()!=null) user.setCity_2(requestDto.getCity_2());
        if(requestDto.getTown_2()!=null) user.setTown_2(requestDto.getTown_2());
    }

    @Transactional
    public void delete(SignedUser signedUser, Integer id, HttpSession session){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        if(session==null) throw new CustomException(ErrorCode.SESSION_NULL);
        // compare id, signed user
        User user=userRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if(!user.getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);

        // delete logic
        userRepository.delete(user);
        session.invalidate();
    }

    public String getImage(Integer id){
        User user=userRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        return user.getImage();
    }
    @Transactional
    public void updateImage(SignedUser signedUser,Integer userId,MultipartFile file) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        User user=userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if(!this.getUser(userId).getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);
        if(file==null) throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);

        // update
        // delete remain image
        if(user.getImage()==null) throw new CustomException(ErrorCode.USER_IMAGE_NOT_FOUND);
        deleteImage(signedUser,userId);
        // new image
        String image=fileHandler.parseUserImage(file);
        user.setImage(image);
    }
    @Transactional
    public void deleteImage(SignedUser signedUser,Integer userId) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        User user=userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if(!this.getUser(userId).getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);
        if(user.getImage()==null) throw new CustomException(ErrorCode.USER_IMAGE_NOT_FOUND);
        // delete
        fileHandler.delete(Constants.userImagePath,user.getImage());
        user.setImage(null);
    }
    public void saveLike(SignedUser signedUser,Integer userId,Integer dealPostId){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        if(!this.getUser(userId).getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);
        if(!dealPostRepository.existsById(dealPostId)) throw new CustomException(ErrorCode.DEALPOST_NOT_FOUND);
        // save
        userLikeService.save(userId,dealPostId);
    }

    public List<Integer> getLikes(Integer userId){
        // check
        User user=userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        // get
        return user.getUserLikes().stream()
                .map(UserLike::getDealPost)
                .map(DealPost::getId)
                .collect(Collectors.toList());
    }
    public void deleteLike(SignedUser signedUser,Integer userId,Integer dealPostId){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        if(!this.getUser(userId).getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);
        if(!dealPostRepository.existsById(dealPostId)) throw new CustomException(ErrorCode.DEALPOST_NOT_FOUND);
        // delete
        userLikeService.delete(userId,dealPostId);
    }

    public List<DealPostGetResponseDto> getDealPosts(Integer userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        return user.getDealPosts().stream()
                .map(DealPost::getId)
                .map(dealPostService::getDealPostDto)
                .collect(Collectors.toList());
    }
}
