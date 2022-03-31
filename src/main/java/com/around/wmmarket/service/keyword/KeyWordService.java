package com.around.wmmarket.service.keyword;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.keyword.KeyWordGetResponseDto;
import com.around.wmmarket.controller.dto.keyword.KeyWordSaveResponseDto;
import com.around.wmmarket.domain.keyword.Keyword;
import com.around.wmmarket.domain.keyword.KeywordRepository;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class KeyWordService {
    private final KeywordRepository keywordRepository;
    private final UserRepository userRepository;

    // save
    @Transactional
    public KeyWordSaveResponseDto save(SignedUser signedUser, String word){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        User user=userRepository.findByEmail(signedUser.getUsername())
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if(keywordRepository.findByUserAndWord(user,word).isPresent()) throw new CustomException(ErrorCode.DUPLICATED_RESOURCE,"중복된 키워드가 존재합니다.");
        // save
        Keyword keyword=keywordRepository.save(Keyword.builder()
                    .user(user)
                    .word(word)
                    .build()
        );
        return KeyWordSaveResponseDto.builder()
                .id(keyword.getId())
                .build();
    }
    // get
    public KeyWordGetResponseDto getKeyWordGetResponse(SignedUser signedUser,Integer keywordId){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        if(keywordRepository.findById(keywordId).isEmpty()) return null;
        Keyword keyword=keywordRepository.findById(keywordId).get();
        if(keyword.getUser()==null
                || !keyword.getUser().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_KEYWORD);
        return KeyWordGetResponseDto.builder()
                .id(keyword.getId())
                .userId(keyword.getUser()!=null?keyword.getUser().getId():null)
                .userNickname(keyword.getUser()!=null?keyword.getUser().getNickname():null)
                .word(keyword.getWord())
                .createdDate(keyword.getCreatedDate())
                .build();
    }
    public KeyWordGetResponseDto getKeyWordGetResponse(SignedUser signedUser,String word){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        User user=userRepository.findByEmail(signedUser.getUsername())
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if(keywordRepository.findByUserAndWord(user,word).isEmpty()) return null;
        Keyword keyword=keywordRepository.findByUserAndWord(user,word).get();
        if(keyword.getUser()==null
                || !keyword.getUser().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_KEYWORD);
        return KeyWordGetResponseDto.builder()
                .id(keyword.getId())
                .userId(keyword.getUser()!=null?keyword.getUser().getId():null)
                .userNickname(keyword.getUser()!=null?keyword.getUser().getNickname():null)
                .word(keyword.getWord())
                .createdDate(keyword.getCreatedDate())
                .build();
    }
    // delete
    @Transactional
    public void delete(SignedUser signedUser,Integer keywordId){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        User user=userRepository.findByEmail(signedUser.getUsername())
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        Keyword keyword=keywordRepository.findById(keywordId)
                .orElseThrow(()->new CustomException(ErrorCode.KEYWORD_NOT_FOUND));
        if(keyword.getUser()==null
                || !keyword.getUser().equals(user)) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_KEYWORD);
        // delete
        keywordRepository.delete(keyword);
    }
    @Transactional
    public void deleteByWord(SignedUser signedUser,String word){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        User user=userRepository.findByEmail(signedUser.getUsername())
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        Keyword keyword=keywordRepository.findByUserAndWord(user,word)
                .orElseThrow(()->new CustomException(ErrorCode.KEYWORD_NOT_FOUND));
        if(keyword.getUser()==null
                || !keyword.getUser().equals(user)) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_KEYWORD);
        // delete
        keywordRepository.delete(keyword);
    }
}
