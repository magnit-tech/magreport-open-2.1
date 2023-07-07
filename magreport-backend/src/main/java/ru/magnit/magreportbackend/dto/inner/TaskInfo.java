package ru.magnit.magreportbackend.dto.inner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@Accessors(chain = true)
public class TaskInfo {
        private String userName;
        private Long reportJobId;

        @Override
        public String toString() {
                return "userName=" + userName + ", reportJobId=" + reportJobId ;
        }
}
