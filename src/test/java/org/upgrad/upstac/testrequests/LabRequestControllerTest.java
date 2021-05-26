package org.upgrad.upstac.testrequests;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.LabRequestController;
import org.upgrad.upstac.testrequests.lab.TestStatus;
import org.upgrad.upstac.users.User;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;
@SpringBootTest
@Slf4j
@PreAuthorize("hasAnyRole('TESTER')")
class LabRequestControllerTest {


    @Autowired
    TestRequestQueryService testRequestQueryService;

    @Autowired
    TestRequestUpdateService testRequestUpdateService;

     @Autowired
     private TestRequestFlowService testRequestFlowService;
    @Autowired
    private UserLoggedInService userLoggedInService;
    @GetMapping ("/to-be-tested")
    @PreAuthorize("hasAnyRole('TESTER')")
    public List<TestRequest> getForTests()
    {

        return testRequestQueryService.findBy(RequestStatus.INITIATED);
    }
      @GetMapping
    @PreAuthorize("hasAnyRole('TESTER')")
      public List<TestRequest> getForTester() {
          User loggedinUser = userLoggedInService.getLoggedInUser();
          return testRequestQueryService.findByTester(loggedinUser);
      }
    @PreAuthorize("hasAnyRole('TESTER')")
            @PutMapping("/assign/{id}")
            public TestRequest assignForLabTest(@PathVariable Long id)
    {
        User tester = userLoggedInService.getLoggedInUser();
        return testRequestUpdateService.assignForLabTest(id,tester);

    }
    @PreAuthorize("hasAnyRole('TESTER')")
    @PutMapping("/update/{id}")
              public TestRequest updateLabTest(@PathVariable Long id,@RequestBody CreateLabResult createLabResult)


        {
            try{
                User tester = userLoggedInService.getLoggedInUser();

                return testRequestUpdateService.updateLabTest(id,createLabResult,tester);

        }



            catch (ConstraintViolationException e)
            {
                throw asConstraintViolation(e);
            }
            catch (AppException e)
            {
                throw asBadRequest(e.getMessage());
            }
        }
}

