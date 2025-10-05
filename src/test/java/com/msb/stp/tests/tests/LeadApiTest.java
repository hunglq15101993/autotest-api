//package com.msb.stp.tests.tests;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//
//import com.msb.stp.tests.endpoints.Routes;
//import com.msb.stp.tests.payload.LeadDto;
//import com.msb.stp.tests.utils.TestBase;
//
//import io.restassured.response.Response;
//
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class LeadApiTest {
//
//    @Test
//    @Order(1)
//    @Tag("smoke")
//    @DisplayName("Test API W4 get Card")
//    void testInquiryCardLiability() {
//        Response response = TestBase.getRequestSpec()
//                .header("X-Request-ID", "")
//                .body("{}")
//                .get(Routes.INQUIRY_CARD_LIABILITY.replace("{cardId}", "123562"));
//
//        response.then().log().all();
//        assertEquals(500, response.statusCode(), "Status code mismatch for Inquiry Card Liability");
//    }
//
//    @Test
//    @Order(2)
//    @Tag("regression")
//    @DisplayName("Test API A05 OpRisk")
//    void testInquiryA05() {
//        LeadDto request = new LeadDto(
//                "202409121448910",
//                "RB",
//                "NOIBO",
//                "0393483175",
//                "79307001"
//        );
//
//        Response response = TestBase.getRequestSpec()
//                .header("Msb-Api-Key", "y1BlmrmGwf0Dx9PO5j6PouvUIeAFW2b4")
//                .body(request)
//                .post(Routes.INQUIRY_A05);
//
//        response.then().log().all();
//        assertEquals(200, response.statusCode(), "Status code mismatch for Inquiry A05");
//    }
//}
