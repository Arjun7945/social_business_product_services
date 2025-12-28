import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('RemovedUser e2e test', () => {
  const removedUserPageUrl = '/removed-user';
  const removedUserPageUrlPattern = new RegExp('/removed-user(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const removedUserSample = {};

  let removedUser;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/removed-users+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/removed-users').as('postEntityRequest');
    cy.intercept('DELETE', '/api/removed-users/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (removedUser) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/removed-users/${removedUser.id}`,
      }).then(() => {
        removedUser = undefined;
      });
    }
  });

  it('RemovedUsers menu should load RemovedUsers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('removed-user');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('RemovedUser').should('exist');
    cy.url().should('match', removedUserPageUrlPattern);
  });

  describe('RemovedUser page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(removedUserPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create RemovedUser page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/removed-user/new$'));
        cy.getEntityCreateUpdateHeading('RemovedUser');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', removedUserPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/removed-users',
          body: removedUserSample,
        }).then(({ body }) => {
          removedUser = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/removed-users+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/removed-users?page=0&size=20>; rel="last",<http://localhost/api/removed-users?page=0&size=20>; rel="first"',
              },
              body: [removedUser],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(removedUserPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details RemovedUser page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('removedUser');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', removedUserPageUrlPattern);
      });

      it('edit button click should load edit RemovedUser page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RemovedUser');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', removedUserPageUrlPattern);
      });

      it('edit button click should load edit RemovedUser page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RemovedUser');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', removedUserPageUrlPattern);
      });

      it('last delete button click should delete instance of RemovedUser', () => {
        cy.intercept('GET', '/api/removed-users/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('removedUser').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', removedUserPageUrlPattern);

        removedUser = undefined;
      });
    });
  });

  describe('new RemovedUser page', () => {
    beforeEach(() => {
      cy.visit(`${removedUserPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('RemovedUser');
    });

    it('should create an instance of RemovedUser', () => {
      cy.get(`[data-cy="originalId"]`).type('13972');
      cy.get(`[data-cy="originalId"]`).should('have.value', '13972');

      cy.get(`[data-cy="name"]`).type('eek');
      cy.get(`[data-cy="name"]`).should('have.value', 'eek');

      cy.get(`[data-cy="role"]`).select('EXECUTIVE');

      cy.get(`[data-cy="whatsappNumber"]`).type('fast lovingly dial');
      cy.get(`[data-cy="whatsappNumber"]`).should('have.value', 'fast lovingly dial');

      cy.get(`[data-cy="phoneNumber"]`).type('swim');
      cy.get(`[data-cy="phoneNumber"]`).should('have.value', 'swim');

      cy.get(`[data-cy="address"]`).type('unless');
      cy.get(`[data-cy="address"]`).should('have.value', 'unless');

      cy.get(`[data-cy="locationLat"]`).type('24868.66');
      cy.get(`[data-cy="locationLat"]`).should('have.value', '24868.66');

      cy.get(`[data-cy="locationLon"]`).type('10123.91');
      cy.get(`[data-cy="locationLon"]`).should('have.value', '10123.91');

      cy.get(`[data-cy="joinedAt"]`).type('2025-12-27T20:53');
      cy.get(`[data-cy="joinedAt"]`).blur();
      cy.get(`[data-cy="joinedAt"]`).should('have.value', '2025-12-27T20:53');

      cy.get(`[data-cy="removedAt"]`).type('2025-12-27T17:02');
      cy.get(`[data-cy="removedAt"]`).blur();
      cy.get(`[data-cy="removedAt"]`).should('have.value', '2025-12-27T17:02');

      cy.get(`[data-cy="reasonForRemoval"]`).type('unfortunate');
      cy.get(`[data-cy="reasonForRemoval"]`).should('have.value', 'unfortunate');

      cy.get(`[data-cy="lastSessionData"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="lastSessionData"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="status"]`).select('RESTORE_ACCOUNT');

      cy.get(`[data-cy="orderHistoryId"]`).type('15991');
      cy.get(`[data-cy="orderHistoryId"]`).should('have.value', '15991');

      cy.get(`[data-cy="distanceFromBusinessKm"]`).type('30993.98');
      cy.get(`[data-cy="distanceFromBusinessKm"]`).should('have.value', '30993.98');

      cy.get(`[data-cy="isPincodeValid"]`).should('not.be.checked');
      cy.get(`[data-cy="isPincodeValid"]`).click();
      cy.get(`[data-cy="isPincodeValid"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        removedUser = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', removedUserPageUrlPattern);
    });
  });
});
