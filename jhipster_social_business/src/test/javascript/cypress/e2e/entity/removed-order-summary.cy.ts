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

describe('RemovedOrderSummary e2e test', () => {
  const removedOrderSummaryPageUrl = '/removed-order-summary';
  const removedOrderSummaryPageUrlPattern = new RegExp('/removed-order-summary(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const removedOrderSummarySample = {};

  let removedOrderSummary;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/removed-order-summaries+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/removed-order-summaries').as('postEntityRequest');
    cy.intercept('DELETE', '/api/removed-order-summaries/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (removedOrderSummary) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/removed-order-summaries/${removedOrderSummary.id}`,
      }).then(() => {
        removedOrderSummary = undefined;
      });
    }
  });

  it('RemovedOrderSummaries menu should load RemovedOrderSummaries page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('removed-order-summary');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('RemovedOrderSummary').should('exist');
    cy.url().should('match', removedOrderSummaryPageUrlPattern);
  });

  describe('RemovedOrderSummary page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(removedOrderSummaryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create RemovedOrderSummary page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/removed-order-summary/new$'));
        cy.getEntityCreateUpdateHeading('RemovedOrderSummary');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', removedOrderSummaryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/removed-order-summaries',
          body: removedOrderSummarySample,
        }).then(({ body }) => {
          removedOrderSummary = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/removed-order-summaries+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/removed-order-summaries?page=0&size=20>; rel="last",<http://localhost/api/removed-order-summaries?page=0&size=20>; rel="first"',
              },
              body: [removedOrderSummary],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(removedOrderSummaryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details RemovedOrderSummary page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('removedOrderSummary');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', removedOrderSummaryPageUrlPattern);
      });

      it('edit button click should load edit RemovedOrderSummary page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RemovedOrderSummary');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', removedOrderSummaryPageUrlPattern);
      });

      it('edit button click should load edit RemovedOrderSummary page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RemovedOrderSummary');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', removedOrderSummaryPageUrlPattern);
      });

      it('last delete button click should delete instance of RemovedOrderSummary', () => {
        cy.intercept('GET', '/api/removed-order-summaries/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('removedOrderSummary').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', removedOrderSummaryPageUrlPattern);

        removedOrderSummary = undefined;
      });
    });
  });

  describe('new RemovedOrderSummary page', () => {
    beforeEach(() => {
      cy.visit(`${removedOrderSummaryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('RemovedOrderSummary');
    });

    it('should create an instance of RemovedOrderSummary', () => {
      cy.get(`[data-cy="userOriginalId"]`).type('19479');
      cy.get(`[data-cy="userOriginalId"]`).should('have.value', '19479');

      cy.get(`[data-cy="userName"]`).type('where');
      cy.get(`[data-cy="userName"]`).should('have.value', 'where');

      cy.get(`[data-cy="userRole"]`).select('DEVELOPER');

      cy.get(`[data-cy="totalOrders"]`).type('10964');
      cy.get(`[data-cy="totalOrders"]`).should('have.value', '10964');

      cy.get(`[data-cy="totalAmount"]`).type('22441.39');
      cy.get(`[data-cy="totalAmount"]`).should('have.value', '22441.39');

      cy.get(`[data-cy="firstInteractionAt"]`).type('2025-12-27T20:17');
      cy.get(`[data-cy="firstInteractionAt"]`).blur();
      cy.get(`[data-cy="firstInteractionAt"]`).should('have.value', '2025-12-27T20:17');

      cy.get(`[data-cy="lastInteractionAt"]`).type('2025-12-28T05:35');
      cy.get(`[data-cy="lastInteractionAt"]`).blur();
      cy.get(`[data-cy="lastInteractionAt"]`).should('have.value', '2025-12-28T05:35');

      cy.get(`[data-cy="removedAt"]`).type('2025-12-28T05:11');
      cy.get(`[data-cy="removedAt"]`).blur();
      cy.get(`[data-cy="removedAt"]`).should('have.value', '2025-12-28T05:11');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        removedOrderSummary = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', removedOrderSummaryPageUrlPattern);
    });
  });
});
