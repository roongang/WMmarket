package com.around.wmmarket.service.user;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.user.UserGetResponseDto;
import com.around.wmmarket.controller.dto.user.UserSaveRequestDto;
import com.around.wmmarket.controller.dto.user.UserUpdateRequestDto;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.domain.user_like.UserLike;
import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.FileHandler;
import com.around.wmmarket.service.userLike.UserLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserLikeService userLikeService;
    private final FileHandler fileHandler;

    @Transactional
    public void save(UserSaveRequestDto requestDto){
        // check duplicate user
        if(isExist(requestDto.getEmail())) throw new CustomException(ErrorCode.DUPLICATE_USER_EMAIL);

        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .role(requestDto.getRole())
                .city_1(requestDto.getCity_1())
                .town_1(requestDto.getTown_1())
                .city_2(requestDto.getCity_2())
                .town_2(requestDto.getTown_2())
                .build();
        if(requestDto.getImage()!=null) user.setImage(fileHandler.parseUserImage(requestDto.getImage()));
        userRepository.save(user);
    }

    public boolean isExist(String email){
        return userRepository.existsByEmail(email);
    }
    public boolean isExist(Integer id) { return userRepository.existsById(id); }

    public UserGetResponseDto getUserResponseDto(String email){
        User user = userRepository.findByEmail(email)
                .orElse(null);
        if(user==null) return null;
        return UserGetResponseDto.builder()
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
    public void update(String email, UserUpdateRequestDto requestDto) {
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if(requestDto.getPassword()!=null) user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        if(requestDto.getNickname()!=null) user.setNickname(requestDto.getNickname());
        if(requestDto.getRole()!=null) user.setRole(requestDto.getRole());
        if(requestDto.getCity_1()!=null) user.setCity_1(requestDto.getCity_1());
        if(requestDto.getTown_1()!=null) user.setTown_1(requestDto.getTown_1());
        if(requestDto.getCity_2()!=null) user.setCity_2(requestDto.getCity_2());
        if(requestDto.getTown_2()!=null) user.setTown_2(requestDto.getTown_2());
    }

    @Transactional
    public void delete(String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }

    public String getImage(String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        return user.getImage();
    }
    public void updateImage(String email,MultipartFile file) {
        // check
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if(file==null) //throw new CustomException(ErrorCode.PARAMETER_NULL);
        // delete remain image
        if(user.getImage()==null) throw new CustomException(ErrorCode.USER_IMAGE_NOT_FOUND);
        try { deleteImage(email); } catch (Exception e) { throw new CustomException(ErrorCode.DELETE_FAIL); }
        // new image
        String image=fileHandler.parseUserImage(file);
        user.setImage(image);
    }
    public void deleteImage(String email) throws Exception{
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if(user.getImage()!=null) fileHandler.delete(Constants.userImagePath,user.getImage());
        user.setImage(null);
    }

    public List<Integer> getLikesDealPostId(Integer userId){
        User user=getUser(userId);
        List<Integer> dealPostIds=new ArrayList<>();
        for(UserLike userLike:user.getUserLikes()){
            dealPostIds.add(userLike.getDealPost().getId());
        }
        return dealPostIds;
    }

    public void deleteLike(Integer userId,Integer dealPostId){
        userLikeService.delete(userId,dealPostId);
    }
}
