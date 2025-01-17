package com.simsimbookstore.apiserver.books.tag.service;

import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import com.simsimbookstore.apiserver.books.tag.dto.TagRequestDto;
import com.simsimbookstore.apiserver.books.tag.dto.TagResponseDto;
import com.simsimbookstore.apiserver.books.tag.mapper.TagMapper;
import com.simsimbookstore.apiserver.books.tag.repository.TagRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    /**
     * 태그를 저장하는 메서드
     *
     * @param tagRequestDto 요청 들어온 태그정보
     * @return 저장한 태그 정보
     */
    @Transactional
    @Override
    public TagResponseDto createTag(TagRequestDto tagRequestDto) {
        Tag tag = TagMapper.toTag(tagRequestDto);
        //중복처리
        Optional<Tag> optionalTag = tagRepository.findByTagName(tag.getTagName());

        if (optionalTag.isPresent()) {
            Tag findTag = optionalTag.get();
            findTag.setActivated(true);
            return TagMapper.toTagResponseDto(findTag);
        } else {
            tag.setActivated(true);
            Tag saveTag = tagRepository.save(tag);
            return TagMapper.toTagResponseDto(saveTag);
        }
    }

    /**
     * 모든 태그 가져오기
     *
     * @return
     */
    @Override
    public List<TagResponseDto> getAlltag() {
        List<Tag> tags = tagRepository.findAllActivated();
        return tags.stream()
                .map(TagMapper::toTagResponseDto)
                .toList();
    }


    /**
     * 태그 하나 가져오기
     *
     * @param tagId
     * @return
     */
    @Override
    public Tag getTag(Long tagId) {
        Optional<Tag> optionalTag = tagRepository.findById(tagId);
        if (optionalTag.isPresent()) {
            return optionalTag.get();
        } else {
            throw new NotFoundException("찾는 태그가 없습니다");
        }
    }




    /**
     * 태그삭제
     *
     * @param tagId
     */
    @Transactional
    @Override
    public void deleteTag(Long tagId) {
        Tag tag = this.getTag(tagId);
        tag.setActivated(false);
    }

    @Transactional
    @Override
    public TagResponseDto updateTag(Long tagId, TagRequestDto requestDto) {
        Tag tag = this.getTag(tagId);
        tag.setTagName(requestDto.getTagName());
        Tag updateTag = tagRepository.save(tag);

        return TagMapper.toTagResponseDto(updateTag);
    }
}
