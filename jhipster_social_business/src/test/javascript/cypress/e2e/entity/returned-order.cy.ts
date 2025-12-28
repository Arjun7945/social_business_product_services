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

describe('ReturnedOrder e2e test', () => {
  const returnedOrderPageUrl = '/returned-order';
  const returnedOrderPageUrlPattern = new RegExp('/returned-order(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const returnedOrderSample = { returnDate: '2025-12-27T19:37:41.289Z', productClaimStatus: 'PENDING_RECEIPT' };

  let returnedOrder;
  let customerOrder;
  let customer;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/customer-orders',
      body: {
        orderTime: '2025-12-27T22:27:46.547Z',
        totalAmount: 20336.34,
        status: 'ORDER_DELIVERED_SUCESSFULLY',
        paymentMethod: 'amazing hm that',
        confirmedAt: '2025-12-28T05:37:52.562Z',
        removedCustomerId: 6207,
        removedDeliveryPersonId: 8626,
        transactionId: 'irritably yawningly',
      },
    }).then(({ body }) => {
      customerOrder = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/customers',
      body: {
        waPhoneNumber: 'deliquesce',
        name: 'given if',
        phoneNumber: 'accomplished',
        locationLat: 4440.38,
        locationLon: 11569.44,
        address: 'healthily',
        distanceFromBusinessKm: 7249.53,
        isPincodeValid: true,
        role: 'CUSTOMER',
        joinedAt: '2025-12-27T14:34:57.915Z',
        lastInteractionAt: '2025-12-27T18:40:54.575Z',
      },
    }).then(({ body }) => {
      customer = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/returned-orders+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/returned-orders').as('postEntityRequest');
    cy.intercept('DELETE', '/api/returned-orders/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/return-status-histories', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/returned-order-items', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/customer-orders', {
      statusCode: 200,
      body: [customerOrder],
    });

    cy.intercept('GET', '/api/customers', {
      statusCode: 200,
      body: [customer],
    });
  });

  afterEach(() => {
    if (returnedOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/returned-orders/${returnedOrder.id}`,
      }).then(() => {
        returnedOrder = undefined;
      });
    }
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
    if (customer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/customers/${customer.id}`,
      }).then(() => {
        customer = undefined;
      });
    }
  });

  it('ReturnedOrders menu should load ReturnedOrders page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('returned-order');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ReturnedOrder').should('exist');
    cy.url().should('match', returnedOrderPageUrlPattern);
  });

  describe('ReturnedOrder page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(returnedOrderPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ReturnedOrder page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/returned-order/new$'));
        cy.getEntityCreateUpdateHeading('ReturnedOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnedOrderPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/returned-orders',
          body: {
            ...returnedOrderSample,
            order: customerOrder,
            customer,
          },
        }).then(({ body }) => {
          returnedOrder = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/returned-orders+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/returned-orders?page=0&size=20>; rel="last",<http://localhost/api/returned-orders?page=0&size=20>; rel="first"',
              },
              body: [returnedOrder],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(returnedOrderPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ReturnedOrder page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('returnedOrder');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnedOrderPageUrlPattern);
      });

      it('edit button click should load edit ReturnedOrder page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ReturnedOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnedOrderPageUrlPattern);
      });

      it('edit button click should load edit ReturnedOrder page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ReturnedOrder');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnedOrderPageUrlPattern);
      });

      it('last delete button click should delete instance of ReturnedOrder', () => {
        cy.intercept('GET', '/api/returned-orders/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('returnedOrder').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnedOrderPageUrlPattern);

        returnedOrder = undefined;
      });
    });
  });

  describe('new ReturnedOrder page', () => {
    beforeEach(() => {
      cy.visit(`${returnedOrderPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ReturnedOrder');
    });

    it('should create an instance of ReturnedOrder', () => {
      cy.get(`[data-cy="returnDate"]`).type('2025-12-27T21:00');
      cy.get(`[data-cy="returnDate"]`).blur();
      cy.get(`[data-cy="returnDate"]`).should('have.value', '2025-12-27T21:00');

      cy.get(`[data-cy="paymentReceivedMode"]`).type('opposite');
      cy.get(`[data-cy="paymentReceivedMode"]`).should('have.value', 'opposite');

      cy.get(`[data-cy="paymentReturnedMode"]`).type('better phew');
      cy.get(`[data-cy="paymentReturnedMode"]`).should('have.value', 'better phew');

      cy.get(`[data-cy="productClaimStatus"]`).select('RECEIVED_DAMAGED');

      cy.get(`[data-cy="refundAmount"]`).type('14193.78');
      cy.get(`[data-cy="refundAmount"]`).should('have.value', '14193.78');

      cy.get(`[data-cy="order"]`).select(1);
      cy.get(`[data-cy="customer"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        returnedOrder = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', returnedOrderPageUrlPattern);
    });
  });
});
