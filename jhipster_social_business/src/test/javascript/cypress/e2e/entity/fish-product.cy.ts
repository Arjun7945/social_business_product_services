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

describe('FishProduct e2e test', () => {
  const fishProductPageUrl = '/fish-product';
  const fishProductPageUrlPattern = new RegExp('/fish-product(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const fishProductSample = { name: 'closely silently where', pricePerKg: 31237.31, isAvailable: false };

  let fishProduct;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/fish-products+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/fish-products').as('postEntityRequest');
    cy.intercept('DELETE', '/api/fish-products/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (fishProduct) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/fish-products/${fishProduct.id}`,
      }).then(() => {
        fishProduct = undefined;
      });
    }
  });

  it('FishProducts menu should load FishProducts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('fish-product');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('FishProduct').should('exist');
    cy.url().should('match', fishProductPageUrlPattern);
  });

  describe('FishProduct page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(fishProductPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create FishProduct page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/fish-product/new$'));
        cy.getEntityCreateUpdateHeading('FishProduct');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', fishProductPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/fish-products',
          body: fishProductSample,
        }).then(({ body }) => {
          fishProduct = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/fish-products+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/fish-products?page=0&size=20>; rel="last",<http://localhost/api/fish-products?page=0&size=20>; rel="first"',
              },
              body: [fishProduct],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(fishProductPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details FishProduct page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('fishProduct');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', fishProductPageUrlPattern);
      });

      it('edit button click should load edit FishProduct page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FishProduct');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', fishProductPageUrlPattern);
      });

      it('edit button click should load edit FishProduct page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FishProduct');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', fishProductPageUrlPattern);
      });

      it('last delete button click should delete instance of FishProduct', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('fishProduct').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', fishProductPageUrlPattern);

        fishProduct = undefined;
      });
    });
  });

  describe('new FishProduct page', () => {
    beforeEach(() => {
      cy.visit(`${fishProductPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('FishProduct');
    });

    it('should create an instance of FishProduct', () => {
      cy.get(`[data-cy="name"]`).type('forenenst but excluding');
      cy.get(`[data-cy="name"]`).should('have.value', 'forenenst but excluding');

      cy.get(`[data-cy="pricePerKg"]`).type('7311.8');
      cy.get(`[data-cy="pricePerKg"]`).should('have.value', '7311.8');

      cy.get(`[data-cy="imageUrl"]`).type('utilization');
      cy.get(`[data-cy="imageUrl"]`).should('have.value', 'utilization');

      cy.get(`[data-cy="description"]`).type('psst');
      cy.get(`[data-cy="description"]`).should('have.value', 'psst');

      cy.get(`[data-cy="isAvailable"]`).should('not.be.checked');
      cy.get(`[data-cy="isAvailable"]`).click();
      cy.get(`[data-cy="isAvailable"]`).should('be.checked');

      cy.get(`[data-cy="createdAt"]`).type('2025-12-15T11:50');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-12-15T11:50');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        fishProduct = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', fishProductPageUrlPattern);
    });
  });
});
