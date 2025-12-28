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

describe('ReturnedOrderItem e2e test', () => {
  const returnedOrderItemPageUrl = '/returned-order-item';
  const returnedOrderItemPageUrlPattern = new RegExp('/returned-order-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const returnedOrderItemSample = {"quantity":8007.84};

  let returnedOrderItem;
  // let fishProduct;
  // let returnedOrder;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/fish-products',
      body: {"name":"mesh cleverly whereas","pricePerKg":14765.69,"availableQuantity":1526.6,"description":"westernise deadly","isAvailable":true,"createdAt":"2025-12-28T01:58:44.315Z"},
    }).then(({ body }) => {
      fishProduct = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/returned-orders',
      body: {"returnDate":"2025-12-27T13:08:30.050Z","paymentReceivedMode":"without unto","paymentReturnedMode":"whoa","productClaimStatus":"RECEIVED_OK","refundAmount":3576.52},
    }).then(({ body }) => {
      returnedOrder = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/returned-order-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/returned-order-items').as('postEntityRequest');
    cy.intercept('DELETE', '/api/returned-order-items/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/fish-products', {
      statusCode: 200,
      body: [fishProduct],
    });

    cy.intercept('GET', '/api/returned-orders', {
      statusCode: 200,
      body: [returnedOrder],
    });

  });
   */

  afterEach(() => {
    if (returnedOrderItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/returned-order-items/${returnedOrderItem.id}`,
      }).then(() => {
        returnedOrderItem = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (fishProduct) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/fish-products/${fishProduct.id}`,
      }).then(() => {
        fishProduct = undefined;
      });
    }
    if (returnedOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/returned-orders/${returnedOrder.id}`,
      }).then(() => {
        returnedOrder = undefined;
      });
    }
  });
   */

  it('ReturnedOrderItems menu should load ReturnedOrderItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('returned-order-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ReturnedOrderItem').should('exist');
    cy.url().should('match', returnedOrderItemPageUrlPattern);
  });

  describe('ReturnedOrderItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(returnedOrderItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ReturnedOrderItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/returned-order-item/new$'));
        cy.getEntityCreateUpdateHeading('ReturnedOrderItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnedOrderItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/returned-order-items',
          body: {
            ...returnedOrderItemSample,
            product: fishProduct,
            returnedOrder: returnedOrder,
          },
        }).then(({ body }) => {
          returnedOrderItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/returned-order-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/returned-order-items?page=0&size=20>; rel="last",<http://localhost/api/returned-order-items?page=0&size=20>; rel="first"',
              },
              body: [returnedOrderItem],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(returnedOrderItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(returnedOrderItemPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ReturnedOrderItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('returnedOrderItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnedOrderItemPageUrlPattern);
      });

      it('edit button click should load edit ReturnedOrderItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ReturnedOrderItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnedOrderItemPageUrlPattern);
      });

      it('edit button click should load edit ReturnedOrderItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ReturnedOrderItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnedOrderItemPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of ReturnedOrderItem', () => {
        cy.intercept('GET', '/api/returned-order-items/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('returnedOrderItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', returnedOrderItemPageUrlPattern);

        returnedOrderItem = undefined;
      });
    });
  });

  describe('new ReturnedOrderItem page', () => {
    beforeEach(() => {
      cy.visit(`${returnedOrderItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ReturnedOrderItem');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of ReturnedOrderItem', () => {
      cy.get(`[data-cy="quantity"]`).type('18398.35');
      cy.get(`[data-cy="quantity"]`).should('have.value', '18398.35');

      cy.get(`[data-cy="productComment"]`).type('pfft alarmed');
      cy.get(`[data-cy="productComment"]`).should('have.value', 'pfft alarmed');

      cy.get(`[data-cy="product"]`).select(1);
      cy.get(`[data-cy="returnedOrder"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        returnedOrderItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', returnedOrderItemPageUrlPattern);
    });
  });
});
