package ru.magnit.magreportbackend.dto.request.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "Запрос информации о доменной группе")
public class DomainGroupRequest {
    private Long domainId;
    private String groupName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainGroupRequest that = (DomainGroupRequest) o;

        if (!getDomainId().equals(that.getDomainId())) return false;
        return getGroupName().equals(that.getGroupName());
    }

    @Override
    public int hashCode() {
        int result = getDomainId().hashCode();
        result = 31 * result + getGroupName().hashCode();
        return result;
    }
}
