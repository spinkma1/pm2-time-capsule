package cz.cvut.fel.pm2.mappers;

import cz.cvut.fel.pm2.UnlockMethodState;
import cz.cvut.fel.pm2.enums.Role;
import cz.cvut.fel.pm2.enums.State;
import cz.cvut.fel.pm2.enums.Type;
import cz.cvut.fel.pm2.enums.UnlockMethod;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.model.UnlockMethodsDto;
import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.persistence.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Component
public class CapsuleMapperImp implements CapsuleMapper {

    @Override
    public void updateEntity(CapsuleDto capsuleInput, Capsule capsuleEntity) {
        if ( capsuleInput == null ) {
            return;
        }

        if ( capsuleEntity.getUsers() != null ) {
            List<User> list = userDtoListToUserList( capsuleInput.users() );
            if ( list != null ) {
                capsuleEntity.getUsers().clear();
                capsuleEntity.getUsers().addAll( list );
            }
            else {
                capsuleEntity.setUsers( null );
            }
        }
        else {
            List<User> list = userDtoListToUserList( capsuleInput.users() );
            if ( list != null ) {
                capsuleEntity.setUsers( list );
            }
        }
        capsuleEntity.setName( capsuleInput.name() );
        capsuleEntity.setDescription( capsuleInput.description() );
    }

    @Override
    public CapsuleDto toDto(Capsule capsuleEntity) {
        if (capsuleEntity == null) {
            return null;
        }

        Long id = capsuleEntity.getId() != null ? capsuleEntity.getId().longValue() : null;
        Long userId = capsuleEntity.getOwner() != null ? capsuleEntity.getOwner().getId().longValue() : null;
        String name = capsuleEntity.getName();
        String description = capsuleEntity.getDescription();
        Boolean teamWork = capsuleEntity.getType() == Type.PRIVATE;
        Long userFileLimit = capsuleEntity.getCapsuleSize();
        List<UserDto> users = userListToUserDtoList(capsuleEntity.getUsers());

        String unlockTime = capsuleEntity.getUnlockTime() != null ? capsuleEntity.getUnlockTime().toString() : null;
        String qrCodePassword = capsuleEntity.getQrCodePassword();
        Double unlockLat = capsuleEntity.getUnlockLat();
        Double unlockLongit = capsuleEntity.getUnlockLongit();

        UnlockMethodsDto unlockMethodsDto = mapUnlockMethods(capsuleEntity.getUnlockMethods());


        String state = capsuleEntity.getState() != null ? capsuleEntity.getState().name() : null;

        return new CapsuleDto(
                id,
                userId,
                name,
                description,
                teamWork,
                userFileLimit,
                unlockTime,
                qrCodePassword,
                unlockLat,
                unlockLongit,
                users,
                unlockMethodsDto,
                state
        );
    }


    @Override
    public List<CapsuleDto> toDtos(List<Capsule> capsuleEntities) {
        if ( capsuleEntities == null ) {
            return null;
        }

        List<CapsuleDto> list = new ArrayList<CapsuleDto>( capsuleEntities.size() );
        for ( Capsule capsule : capsuleEntities ) {
            list.add( toDto( capsule ) );
        }

        return list;
    }

    @Override
    public Capsule toEntity(CapsuleDto capsuleDto) {
        if ( capsuleDto == null ) {
            return null;
        }

        Capsule capsule = new Capsule();


        capsule.setUsers( userDtoListToUserList( capsuleDto.users() ) );
        capsule.setName( capsuleDto.name() );
        capsule.setDescription( capsuleDto.description() );

        return capsule;
    }

    protected List<Capsule> capsuleDtoListToCapsuleList(List<CapsuleDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Capsule> list1 = new ArrayList<Capsule>( list.size() );
        for ( CapsuleDto capsuleDto : list ) {
            list1.add( toEntity( capsuleDto ) );
        }

        return list1;
    }

    protected List<User> userDtoListToUserList(List<UserDto> list) {
        if ( list == null ) {
            return null;
        }

        List<User> list1 = new ArrayList<User>( list.size() );
        for ( UserDto userDto : list ) {
            list1.add( userDtoToUser( userDto ) );
        }

        return list1;
    }

    protected User userDtoToUser(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User user = new User();

        if ( userDto.id() != null ) {
            user.setId( userDto.id().intValue() );
        }
        user.setEmail( userDto.email() );
        if ( userDto.role() != null ) {
            user.setRole( Enum.valueOf( Role.class, userDto.role() ) );
        }
        user.setCapsules( capsuleDtoListToCapsuleList( userDto.capsules() ) );
        user.setFollowers( userDtoListToUserList( userDto.followers() ) );

        return user;
    }

    protected List<UserDto> userListToUserDtoList(List<User> list) {
        if ( list == null ) {
            return null;
        }

        List<UserDto> list1 = new ArrayList<UserDto>( list.size() );
        for ( User user : list ) {
            list1.add( userToUserDto( user ) );
        }

        return list1;
    }

    protected UserDto userToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        Long id = null;
        String email = null;
        String role = null;
        List<UserDto> followers = null;
        List<CapsuleDto> capsules = null;

        if ( user.getId() != null ) {
            id = user.getId().longValue();
        }
        email = user.getEmail();
        if ( user.getRole() != null ) {
            role = user.getRole().name();
        }
        followers = userListToUserDtoList( user.getFollowers() );
        capsules = toDtos( user.getCapsules() );

        UserDto userDto = new UserDto( id, email, role, followers, capsules );

        return userDto;
    }

    private UnlockMethodsDto mapUnlockMethods(Map<UnlockMethod, UnlockMethodState> unlockMethods) {
        UnlockMethodState timeState = unlockMethods.getOrDefault(UnlockMethod.TIME, new UnlockMethodState());
        UnlockMethodState qrCodeState = unlockMethods.getOrDefault(UnlockMethod.QR_CODE, new UnlockMethodState());
        UnlockMethodState geolocationState = unlockMethods.getOrDefault(UnlockMethod.GEOLOCATION, new UnlockMethodState());
        UnlockMethodState passwordState = unlockMethods.getOrDefault(UnlockMethod.PASSWORD, new UnlockMethodState());

        return new UnlockMethodsDto(
                timeState.isEnabled(),
                timeState.isComplete(),
                qrCodeState.isEnabled(),
                qrCodeState.isComplete(),
                geolocationState.isEnabled(),
                geolocationState.isComplete(),
                passwordState.isEnabled(),
                passwordState.isComplete()
        );
    }

}
