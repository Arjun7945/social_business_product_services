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

describe('ReturnStatusHistory e2e test', () => {
  const returnStatusHistoryPageUrl = '/return-status-history';
  const returnStatusHistoryPageUrlPattern = new RegExp('/return-status-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const returnStatusHistorySample = { status: 'REFUND_COMPLETED', changeTime: '2025-12-27T14:08:15.634Z' };

  let returnStatusHistory;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/return-status-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/return-status-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/return-status-histories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (returnStatusHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/return-status-histories/${returnStatusHistory.id}`,
      }).then(() => {
        returnStatusHistory = undefined;
      });
    }
  });

  it('ReturnStatusHistories menu should load ReturnStatusHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('return-status-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ReturnStatusHistory').should('exist');
    cy.url().should('match', returnStatusHistoryPageUrlPattern);
  });

  describe('ReturnStatusHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(returnStatusHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ReturnStatusHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/return-status-history/new$'));
        cy.getEntityCreateUpdateHeading('ReturnStatusHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnStatusHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/return-status-histories',
          body: returnStatusHistorySample,
        }).then(({ body }) => {
          returnStatusHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/return-status-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/return-status-histories?page=0&size=20>; rel="last",<http://localhost/api/return-status-histories?page=0&size=20>; rel="first"',
              },
              body: [returnStatusHistory],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(returnStatusHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ReturnStatusHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('returnStatusHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnStatusHistoryPageUrlPattern);
      });

      it('edit button click should load edit ReturnStatusHistory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ReturnStatusHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnStatusHistoryPageUrlPattern);
      });

      it('edit button click should load edit ReturnStatusHistory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ReturnStatusHistory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnStatusHistoryPageUrlPattern);
      });

      it('last delete button click should delete instance of ReturnStatusHistory', () => {
        cy.intercept('GET', '/api/return-status-histories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('returnStatusHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnStatusHistoryPageUrlPattern);

        returnStatusHistory = undefined;
      });
    });
  });

  describe('new ReturnStatusHistory page', () => {
    beforeEach(() => {
      cy.visit(`${returnStatusHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ReturnStatusHistory');
    });

    it('should create an instance of ReturnStatusHistory', () => {
      cy.get(`[data-cy="status"]`).select('RETURN_APPROVED');

      cy.get(`[data-cy="changeTime"]`).type('2025-12-27T08:50');
      cy.get(`[data-cy="changeTime"]`).blur();
      cy.get(`[data-cy="changeTime"]`).should('have.value', '2025-12-27T08:50');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        returnStatusHistory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', returnStatusHistoryPageUrlPattern);
    });
  });
});
