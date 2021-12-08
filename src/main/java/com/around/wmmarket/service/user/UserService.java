package com.around.wmmarket.service.user;

import com.around.wmmarket.controller.dto.User.UserGetResponseDto;
import com.around.wmmarket.controller.dto.User.UserSaveRequestDto;
import com.around.wmmarket.controller.dto.User.UserUpdateRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.service.common.Constants;
import com.around.wmmarket.service.common.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final FileHandler fileHandler;

    @Transactional
    public void save(UserSaveRequestDto requestDto){
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
        String image= fileHandler.parseUserImage(requestDto.getImage());
        if(image!=null) user.setImage(image);
        userRepository.save(user);
    }

    public boolean isExist(String email){
        return userRepository.existsByEmail(email);
    }

    public UserGetResponseDto getUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
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

    @Transactional
    public void update(String email, UserUpdateRequestDto requestDto) throws Exception{
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("해당 유저가 존재하지 않습니다. email:"+email));
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
                .orElseThrow(()->new UsernameNotFoundException("해당 유저가 존재하지 않습니다. email:"+email));
        userRepository.delete(user);
    }

    public String getImage(String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("해당 유저가 존재하지 않습니다. email:"+email));
        return user.getImage();
    }
    public void updateImage(String email,MultipartFile file) throws Exception{
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("해당 유저가 존재하지 않습니다. email:"+email));
        // delete remain image
        deleteImage(email);
        // new image
        String image=fileHandler.parseUserImage(file);
        user.setImage(image);
    }
    public void deleteImage(String email) throws Exception{
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("해당 유저가 존재하지 않습니다. email:"+email));
        if(user.getImage()!=null) fileHandler.delete(Constants.userImagePath,user.getImage());
        user.setImage(null);
    }
}
