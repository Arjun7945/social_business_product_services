import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITeamMember } from '../team-member.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../team-member.test-samples';

import { TeamMemberService } from './team-member.service';

const requireRestSample: ITeamMember = {
  ...sampleWithRequiredData,
};

describe('TeamMember Service', () => {
  let service: TeamMemberService;
  let httpMock: HttpTestingController;
  let expectedResult: ITeamMember | ITeamMember[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TeamMemberService);
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

    it('should create a TeamMember', () => {
      const teamMember = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(teamMember).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TeamMember', () => {
      const teamMember = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(teamMember).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TeamMember', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TeamMember', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TeamMember', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTeamMemberToCollectionIfMissing', () => {
      it('should add a TeamMember to an empty array', () => {
        const teamMember: ITeamMember = sampleWithRequiredData;
        expectedResult = service.addTeamMemberToCollectionIfMissing([], teamMember);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teamMember);
      });

      it('should not add a TeamMember to an array that contains it', () => {
        const teamMember: ITeamMember = sampleWithRequiredData;
        const teamMemberCollection: ITeamMember[] = [
          {
            ...teamMember,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTeamMemberToCollectionIfMissing(teamMemberCollection, teamMember);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TeamMember to an array that doesn't contain it", () => {
        const teamMember: ITeamMember = sampleWithRequiredData;
        const teamMemberCollection: ITeamMember[] = [sampleWithPartialData];
        expectedResult = service.addTeamMemberToCollectionIfMissing(teamMemberCollection, teamMember);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teamMember);
      });

      it('should add only unique TeamMember to an array', () => {
        const teamMemberArray: ITeamMember[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const teamMemberCollection: ITeamMember[] = [sampleWithRequiredData];
        expectedResult = service.addTeamMemberToCollectionIfMissing(teamMemberCollection, ...teamMemberArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const teamMember: ITeamMember = sampleWithRequiredData;
        const teamMember2: ITeamMember = sampleWithPartialData;
        expectedResult = service.addTeamMemberToCollectionIfMissing([], teamMember, teamMember2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teamMember);
        expect(expectedResult).toContain(teamMember2);
      });

      it('should accept null and undefined values', () => {
        const teamMember: ITeamMember = sampleWithRequiredData;
        expectedResult = service.addTeamMemberToCollectionIfMissing([], null, teamMember, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teamMember);
      });

      it('should return initial array if no TeamMember is added', () => {
        const teamMemberCollection: ITeamMember[] = [sampleWithRequiredData];
        expectedResult = service.addTeamMemberToCollectionIfMissing(teamMemberCollection, undefined, null);
        expect(expectedResult).toEqual(teamMemberCollection);
      });
    });

    describe('compareTeamMember', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTeamMember(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 11287 };
        const entity2 = null;

        const compareResult1 = service.compareTeamMember(entity1, entity2);
        const compareResult2 = service.compareTeamMember(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 11287 };
        const entity2 = { id: 22246 };

        const compareResult1 = service.compareTeamMember(entity1, entity2);
        const compareResult2 = service.compareTeamMember(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 11287 };
        const entity2 = { id: 11287 };

        const compareResult1 = service.compareTeamMember(entity1, entity2);
        const compareResult2 = service.compareTeamMember(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
