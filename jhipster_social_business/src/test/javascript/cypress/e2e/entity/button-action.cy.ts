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

describe('ButtonAction e2e test', () => {
  const buttonActionPageUrl = '/button-action';
  const buttonActionPageUrlPattern = new RegExp('/button-action(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const buttonActionSample = { waMessageId: 'outside than wherever' };

  let buttonAction;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/button-actions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/button-actions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/button-actions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (buttonAction) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/button-actions/${buttonAction.id}`,
      }).then(() => {
        buttonAction = undefined;
      });
    }
  });

  it('ButtonActions menu should load ButtonActions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('button-action');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ButtonAction').should('exist');
    cy.url().should('match', buttonActionPageUrlPattern);
  });

  describe('ButtonAction page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(buttonActionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ButtonAction page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/button-action/new$'));
        cy.getEntityCreateUpdateHeading('ButtonAction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', buttonActionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/button-actions',
          body: buttonActionSample,
        }).then(({ body }) => {
          buttonAction = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/button-actions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/button-actions?page=0&size=20>; rel="last",<http://localhost/api/button-actions?page=0&size=20>; rel="first"',
              },
              body: [buttonAction],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(buttonActionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ButtonAction page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('buttonAction');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', buttonActionPageUrlPattern);
      });

      it('edit button click should load edit ButtonAction page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ButtonAction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', buttonActionPageUrlPattern);
      });

      it('edit button click should load edit ButtonAction page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ButtonAction');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', buttonActionPageUrlPattern);
      });

      it('last delete button click should delete instance of ButtonAction', () => {
        cy.intercept('GET', '/api/button-actions/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('buttonAction').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', buttonActionPageUrlPattern);

        buttonAction = undefined;
      });
    });
  });

  describe('new ButtonAction page', () => {
    beforeEach(() => {
      cy.visit(`${buttonActionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ButtonAction');
    });

    it('should create an instance of ButtonAction', () => {
      cy.get(`[data-cy="waMessageId"]`).type('why ick who');
      cy.get(`[data-cy="waMessageId"]`).should('have.value', 'why ick who');

      cy.get(`[data-cy="buttonId"]`).type('up');
      cy.get(`[data-cy="buttonId"]`).should('have.value', 'up');

      cy.get(`[data-cy="clickedAt"]`).type('2025-12-27T14:45');
      cy.get(`[data-cy="clickedAt"]`).blur();
      cy.get(`[data-cy="clickedAt"]`).should('have.value', '2025-12-27T14:45');

      cy.get(`[data-cy="clickedBy"]`).type('beneficial');
      cy.get(`[data-cy="clickedBy"]`).should('have.value', 'beneficial');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        buttonAction = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', buttonActionPageUrlPattern);
    });
  });
});
