import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IBotSession } from '../bot-session.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../bot-session.test-samples';

import { BotSessionService, RestBotSession } from './bot-session.service';

const requireRestSample: RestBotSession = {
  ...sampleWithRequiredData,
  lastActiveAt: sampleWithRequiredData.lastActiveAt?.toJSON(),
};

describe('BotSession Service', () => {
  let service: BotSessionService;
  let httpMock: HttpTestingController;
  let expectedResult: IBotSession | IBotSession[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BotSessionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a BotSession', () => {
      const botSession = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(botSession).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BotSession', () => {
      const botSession = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(botSession).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BotSession', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BotSession', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BotSession', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBotSessionToCollectionIfMissing', () => {
      it('should add a BotSession to an empty array', () => {
        const botSession: IBotSession = sampleWithRequiredData;
        expectedResult = service.addBotSessionToCollectionIfMissing([], botSession);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(botSession);
      });

      it('should not add a BotSession to an array that contains it', () => {
        const botSession: IBotSession = sampleWithRequiredData;
        const botSessionCollection: IBotSession[] = [
          {
            ...botSession,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBotSessionToCollectionIfMissing(botSessionCollection, botSession);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BotSession to an array that doesn't contain it", () => {
        const botSession: IBotSession = sampleWithRequiredData;
        const botSessionCollection: IBotSession[] = [sampleWithPartialData];
        expectedResult = service.addBotSessionToCollectionIfMissing(botSessionCollection, botSession);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(botSession);
      });

      it('should add only unique BotSession to an array', () => {
        const botSessionArray: IBotSession[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const botSessionCollection: IBotSession[] = [sampleWithRequiredData];
        expectedResult = service.addBotSessionToCollectionIfMissing(botSessionCollection, ...botSessionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const botSession: IBotSession = sampleWithRequiredData;
        const botSession2: IBotSession = sampleWithPartialData;
        expectedResult = service.addBotSessionToCollectionIfMissing([], botSession, botSession2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(botSession);
        expect(expectedResult).toContain(botSession2);
      });

      it('should accept null and undefined values', () => {
        const botSession: IBotSession = sampleWithRequiredData;
        expectedResult = service.addBotSessionToCollectionIfMissing([], null, botSession, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(botSession);
      });

      it('should return initial array if no BotSession is added', () => {
        const botSessionCollection: IBotSession[] = [sampleWithRequiredData];
        expectedResult = service.addBotSessionToCollectionIfMissing(botSessionCollection, undefined, null);
        expect(expectedResult).toEqual(botSessionCollection);
      });
    });

    describe('compareBotSession', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBotSession(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 30587 };
        const entity2 = null;

        const compareResult1 = service.compareBotSession(entity1, entity2);
        const compareResult2 = service.compareBotSession(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 30587 };
        const entity2 = { id: 26689 };

        const compareResult1 = service.compareBotSession(entity1, entity2);
        const compareResult2 = service.compareBotSession(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 30587 };
        const entity2 = { id: 30587 };

        const compareResult1 = service.compareBotSession(entity1, entity2);
        const compareResult2 = service.compareBotSession(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
