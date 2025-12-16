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

describe('ShoppingCart e2e test', () => {
  const shoppingCartPageUrl = '/shopping-cart';
  const shoppingCartPageUrlPattern = new RegExp('/shopping-cart(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const shoppingCartSample = { createdAt: '2025-12-14T21:27:31.994Z' };

  let shoppingCart;
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
        waPhoneNumber: 'duh minor upbeat',
        name: 'qua smug whoa',
        phoneNumber: 'lounge nice',
        locationLat: 30131.51,
        locationLon: 6336.29,
        address: 'term',
        distanceFromBusinessKm: 14058.26,
        isPincodeValid: false,
        role: 'CUSTOMER',
        joinedAt: '2025-12-14T16:46:58.655Z',
        lastInteractionAt: '2025-12-15T00:02:55.313Z',
      },
    }).then(({ body }) => {
      customer = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/shopping-carts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/shopping-carts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/shopping-carts/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/cart-items', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/customers', {
      statusCode: 200,
      body: [customer],
    });
  });

  afterEach(() => {
    if (shoppingCart) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/shopping-carts/${shoppingCart.id}`,
      }).then(() => {
        shoppingCart = undefined;
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

  it('ShoppingCarts menu should load ShoppingCarts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('shopping-cart');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ShoppingCart').should('exist');
    cy.url().should('match', shoppingCartPageUrlPattern);
  });

  describe('ShoppingCart page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(shoppingCartPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ShoppingCart page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/shopping-cart/new$'));
        cy.getEntityCreateUpdateHeading('ShoppingCart');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shoppingCartPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/shopping-carts',
          body: {
            ...shoppingCartSample,
            customer,
          },
        }).then(({ body }) => {
          shoppingCart = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/shopping-carts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/shopping-carts?page=0&size=20>; rel="last",<http://localhost/api/shopping-carts?page=0&size=20>; rel="first"',
              },
              body: [shoppingCart],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(shoppingCartPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ShoppingCart page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('shoppingCart');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shoppingCartPageUrlPattern);
      });

      it('edit button click should load edit ShoppingCart page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShoppingCart');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shoppingCartPageUrlPattern);
      });

      it('edit button click should load edit ShoppingCart page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShoppingCart');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shoppingCartPageUrlPattern);
      });

      it('last delete button click should delete instance of ShoppingCart', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('shoppingCart').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shoppingCartPageUrlPattern);

        shoppingCart = undefined;
      });
    });
  });

  describe('new ShoppingCart page', () => {
    beforeEach(() => {
      cy.visit(`${shoppingCartPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ShoppingCart');
    });

    it('should create an instance of ShoppingCart', () => {
      cy.get(`[data-cy="createdAt"]`).type('2025-12-14T21:50');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-12-14T21:50');

      cy.get(`[data-cy="updatedAt"]`).type('2025-12-14T14:27');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2025-12-14T14:27');

      cy.get(`[data-cy="customer"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        shoppingCart = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', shoppingCartPageUrlPattern);
    });
  });
});
