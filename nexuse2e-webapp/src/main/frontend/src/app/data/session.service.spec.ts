import { SessionService } from "./session.service";
import { DateRange } from "../types";

fdescribe("SessionService", () => {
  let service: SessionService;
  beforeEach(() => {
    service = new SessionService();
  });

  it("should save data and retrieve them", () => {
    const filters = {
      status: "SUCCESS",
      conversationId: "23423b"
    };
    service.setActiveFilters("test", filters);
    expect(service.getActiveFilters("test")).toEqual(filters);
  });

  it("should not change Dates to string when retrieving data", () => {
    const filters = {
      dateRange: {
        startDate: new Date(12345),
        endDate: new Date(678910)
      }
    };
    service.setActiveFilters("test", filters);
    const dateRange = service.getActiveFilters("test")["dateRange"] as DateRange;
    expect(dateRange.startDate).toEqual(new Date(12345));
  });
});
