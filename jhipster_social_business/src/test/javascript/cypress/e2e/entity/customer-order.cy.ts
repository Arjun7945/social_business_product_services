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

describe('CustomerOrder e2e test', () => {
  const customerOrderPageUrl = '/customer-order';
  const customerOrderPageUrlPattern = new RegExp('/customer-order(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const customerOrderSample = { orderTime: '2025-12-15T02:29:21.131Z', totalAmount: 27496.5, status: 'CANCELLED', paymentMethod: 'where' };

  let customerOrder;
  let customer;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/customers',
      body: {
        waPhoneNumber: 'aha naturally into',
        name: 'delete',
        phoneNumber: 'yuck abaft',
        locationLat: 9037.16,
        locationLon: 14818.32,
        address: 'psst',
        distanceFromBusinessKm: 4468.41,
        isPincodeValid: false,
        role: 'DEVELOPER',
        joinedAt: '2025-12-14T19:32:05.275Z',
        lastInteractionAt: '2025-12-14T18:57:53.874Z',
      },
    }).then(({ body }) => {
      customer = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/customer-orders+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/customer-orders').as('postEntityRequest');
    cy.intercept('DELETE', '/api/customer-orders/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/order-items', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/team-members', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/customers', {
      statusCode: 200,
      body: [customer],
    });
  });

  afterEach(() => {
    if (customerOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/customer-orders/${customerOrder.id}`,
      }).then(() => {
        customerOrder = undefined;
      });
    }
  });

  afterEach(() => {
    if (customer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/customers/${customer.id}`,
      }).then(() => {
        customer = undefined;
      });
    }
  });

  it('CustomerOrders menu should load CustomerOrders page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('customer-order');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CustomerOrder').should('exist');
    cy.url().should('match', customerOrderPageUrlPattern);
  });

  describe('CustomerOrder page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(customerOrderPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CustomerOrder page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/customer-order/new$'));
        cy.getEntityCreateUpdateHeading('CustomerOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', customerOrderPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/customer-orders',
          body: {
            ...customerOrderSample,
            customer,
          },
        }).then(({ body }) => {
          customerOrder = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/customer-orders+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/customer-orders?page=0&size=20>; rel="last",<http://localhost/api/customer-orders?page=0&size=20>; rel="first"',
              },
              body: [customerOrder],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(customerOrderPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CustomerOrder page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('customerOrder');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', customerOrderPageUrlPattern);
      });

      it('edit button click should load edit CustomerOrder page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CustomerOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', customerOrderPageUrlPattern);
      });

      it('edit button click should load edit CustomerOrder page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CustomerOrder');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', customerOrderPageUrlPattern);
      });

      it('last delete button click should delete instance of CustomerOrder', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('customerOrder').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', customerOrderPageUrlPattern);

        customerOrder = undefined;
      });
    });
  });

  describe('new CustomerOrder page', () => {
    beforeEach(() => {
      cy.visit(`${customerOrderPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CustomerOrder');
    });

    it('should create an instance of CustomerOrder', () => {
      cy.get(`[data-cy="orderTime"]`).type('2025-12-14T14:09');
      cy.get(`[data-cy="orderTime"]`).blur();
      cy.get(`[data-cy="orderTime"]`).should('have.value', '2025-12-14T14:09');

      cy.get(`[data-cy="totalAmount"]`).type('2987.91');
      cy.get(`[data-cy="totalAmount"]`).should('have.value', '2987.91');

      cy.get(`[data-cy="status"]`).select('PENDING');

      cy.get(`[data-cy="paymentMethod"]`).type('elegantly');
      cy.get(`[data-cy="paymentMethod"]`).should('have.value', 'elegantly');

      cy.get(`[data-cy="confirmedAt"]`).type('2025-12-15T03:03');
      cy.get(`[data-cy="confirmedAt"]`).blur();
      cy.get(`[data-cy="confirmedAt"]`).should('have.value', '2025-12-15T03:03');

      cy.get(`[data-cy="customer"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        customerOrder = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', customerOrderPageUrlPattern);
    });
  });
});
