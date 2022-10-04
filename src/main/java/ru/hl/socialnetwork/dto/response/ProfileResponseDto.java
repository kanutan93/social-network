package ru.hl.socialnetwork.dto.response;

import lombok.Data;
import ru.hl.socialnetwork.enums.SexEnum;

@Data
public class ProfileResponseDto {
  private String email;
  private String firstName;
  private String lastName;
  private short age;
  private SexEnum sex;
  private String aboutInfo;
  private String city;
}
