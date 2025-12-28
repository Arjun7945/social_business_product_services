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

describe('OrderStatusHistory e2e test', () => {
  const orderStatusHistoryPageUrl = '/order-status-history';
  const orderStatusHistoryPageUrlPattern = new RegExp('/order-status-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const orderStatusHistorySample = { status: 'DELIVERY_ONWAY', changeTime: '2025-12-28T01:00:36.537Z' };

  let orderStatusHistory;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/order-status-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/order-status-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/order-status-histories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (orderStatusHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/order-status-histories/${orderStatusHistory.id}`,
      }).then(() => {
        orderStatusHistory = undefined;
      });
    }
  });

  it('OrderStatusHistories menu should load OrderStatusHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('order-status-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('OrderStatusHistory').should('exist');
    cy.url().should('match', orderStatusHistoryPageUrlPattern);
  });

  describe('OrderStatusHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(orderStatusHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create OrderStatusHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/order-status-history/new$'));
        cy.getEntityCreateUpdateHeading('OrderStatusHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderStatusHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/order-status-histories',
          body: orderStatusHistorySample,
        }).then(({ body }) => {
          orderStatusHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/order-status-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/order-status-histories?page=0&size=20>; rel="last",<http://localhost/api/order-status-histories?page=0&size=20>; rel="first"',
              },
              body: [orderStatusHistory],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(orderStatusHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details OrderStatusHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('orderStatusHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderStatusHistoryPageUrlPattern);
      });

      it('edit button click should load edit OrderStatusHistory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('OrderStatusHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderStatusHistoryPageUrlPattern);
      });

      it('edit button click should load edit OrderStatusHistory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('OrderStatusHistory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderStatusHistoryPageUrlPattern);
      });

      it('last delete button click should delete instance of OrderStatusHistory', () => {
        cy.intercept('GET', '/api/order-status-histories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('orderStatusHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderStatusHistoryPageUrlPattern);

        orderStatusHistory = undefined;
      });
    });
  });

  describe('new OrderStatusHistory page', () => {
    beforeEach(() => {
      cy.visit(`${orderStatusHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('OrderStatusHistory');
    });

    it('should create an instance of OrderStatusHistory', () => {
      cy.get(`[data-cy="status"]`).select('ORDER_FAILED');

      cy.get(`[data-cy="changeTime"]`).type('2025-12-27T17:48');
      cy.get(`[data-cy="changeTime"]`).blur();
      cy.get(`[data-cy="changeTime"]`).should('have.value', '2025-12-27T17:48');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        orderStatusHistory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', orderStatusHistoryPageUrlPattern);
    });
  });
});
