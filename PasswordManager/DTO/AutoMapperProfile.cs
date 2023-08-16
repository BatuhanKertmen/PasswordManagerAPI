using AutoMapper;
using PasswordManager.Models;

namespace PasswordManager.DTO;

public class AutoMapperProfile : Profile
{
    public  AutoMapperProfile()
    {
        CreateMap<UserRegisterRequestDto, User>()
            .ForMember(dest => dest.Id, opt => opt.MapFrom(_ => new Guid()))
            .ForMember(dest => dest.Active, opt => opt.MapFrom(_ => false))
            .ForMember(dest => dest.Created, opt => opt.MapFrom(_ => DateTime.UtcNow))
            .ForMember(dest => dest.LastUpdated, opt => opt.MapFrom(_ => DateTime.UtcNow))
            .ForMember(dest => dest.UserPassword, opt => opt.Ignore());
        
        CreateMap<User, UserRegisterResponseDto>();

        CreateMap<string, UserLoginResponseDto>()
            .ForMember(dest => dest.Token, opt => opt.MapFrom(src => src));
        
        CreateMap<LoginInformationRequestDto, LoginInformation>()
            .ForMember(dest => dest.Id, opt => opt.MapFrom(_ => new Guid()))
            .ForMember(dest => dest.PasswordEncrypted, opt => opt.MapFrom(src => src.Password))
            .ForMember(dest => dest.UsernameEncrypted, opt => opt.MapFrom(src => src.UserName))
            .ForMember(dest => dest.Domain, opt => opt.MapFrom(src => src.Domain));

        CreateMap<LoginInformationRequestDto, LoginInformationPassword>()
            .ForMember(dest => dest.Id, opt => opt.MapFrom(_ => new Guid()))
            .ForMember(dest => dest.Iterations, opt => opt.MapFrom(src => src.Iterations))
            .ForMember(dest => dest.DegreeOfParallelism, opt => opt.MapFrom(src => src.DegreeOfParallelism))
            .ForMember(dest => dest.MemorySize, opt => opt.MapFrom(src => src.MemorySize))
            .ForMember(dest => dest.Salt, opt => opt.MapFrom(src => Convert.FromHexString(src.SaltHexString)));

        CreateMap<LoginInformation, LoginInformationResponseDto>();
    }   
}