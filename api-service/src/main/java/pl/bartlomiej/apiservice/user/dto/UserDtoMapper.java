package pl.bartlomiej.apiservice.user.dto;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.bartlomiej.apiservice.user.domain.User;

@Component
public class UserDtoMapper {

    private final ModelMapper modelMapper;

    public UserDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User mapFrom(UserRegisterDto userRegisterDto) {
        return modelMapper.map(userRegisterDto, User.class);
    }

    public UserReadDto mapToReadDto(User user) {
        return modelMapper.map(user, UserReadDto.class);
    }
}
