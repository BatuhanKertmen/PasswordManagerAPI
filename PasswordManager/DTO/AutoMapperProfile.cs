using AutoMapper;
using PasswordManager.Models;

namespace PasswordManager.DTO;

public class AutoMapperProfile : Profile
{
    public  AutoMapperProfile()
    {
        CreateMap<UserRegisterRequestDto, User>()
            .ForMember(dest => dest.Id, opt => opt.MapFrom(src => new Guid()))
            .ForMember(dest => dest.Active, opt => opt.MapFrom(src => false))
            .ForMember(dest => dest.Created, opt => opt.MapFrom(src => DateTime.UtcNow))
            .ForMember(dest => dest.LastUpdated, opt => opt.MapFrom(src => DateTime.UtcNow))
            .ForMember(dest => dest.UserPassword, opt => opt.Ignore());
        
        CreateMap<User, UserRegisterResponseDto>();

        CreateMap<string, UserLoginResponseDto>()
            .ForMember(dest => dest.Token, opt => opt.MapFrom(src => src));
    }
}