package ru.magnit.magreportbackend.dto.request.schedule;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
public class DestinationUserAddRequest {
    String userName;
    String domainName;
    Long userId;
    Long typeId;
}
