package ru.magnit.magreportbackend.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.user.UserStatusEnum;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "Запрос на получение страницы пользователей")
public class UserPageRequest {

    private String searchValue;
    private Long pageNumber;
    private Long usersPerPage;
    private List<UserStatusEnum> statuses = Collections.emptyList();

}
