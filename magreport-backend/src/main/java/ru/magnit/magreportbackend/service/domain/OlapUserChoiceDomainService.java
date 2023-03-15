package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.olap.OlapUserChoice;
import ru.magnit.magreportbackend.domain.report.Report;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.repository.OlapUserChoiceRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class OlapUserChoiceDomainService {

    private final OlapUserChoiceRepository repository;


    @Value("${magreport.olap.default-start-view}")
    private boolean defaultUserChoice;


    public void setOlapUserChoice(Long reportId, Long userId, boolean isLastUserChoice){

        OlapUserChoice userChoice;
        if (repository.existsByReportIdAndUserId(reportId, userId)){
            userChoice = repository.getOlapUserChoiceByReportIdAndUserId(reportId, userId);
        }
        else {
            userChoice = new OlapUserChoice()
                    .setUser(new User(userId))
                    .setReport(new Report(reportId));
        }

        userChoice.setIsLastChoice(isLastUserChoice);
        repository.save(userChoice);
    }

    public boolean getOlapUserChoice(Long reportId, Long userId){

        if(repository.existsByReportIdAndUserId(reportId, userId))
            return repository.getOlapUserChoiceByReportIdAndUserId(reportId, userId).getIsLastChoice();
        else
            return defaultUserChoice;
    }
    @Transactional
    public void deleteUsersChoiceForReport(Long reportId){

        repository.deleteAllByReportId(reportId);

    }
}
