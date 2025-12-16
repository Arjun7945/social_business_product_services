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

describe('BotSession e2e test', () => {
  const botSessionPageUrl = '/bot-session';
  const botSessionPageUrlPattern = new RegExp('/bot-session(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const botSessionSample = { waPhoneNumber: 'fly', currentState: 'brr dally' };

  let botSession;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/bot-sessions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/bot-sessions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/bot-sessions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (botSession) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/bot-sessions/${botSession.id}`,
      }).then(() => {
        botSession = undefined;
      });
    }
  });

  it('BotSessions menu should load BotSessions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('bot-session');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('BotSession').should('exist');
    cy.url().should('match', botSessionPageUrlPattern);
  });

  describe('BotSession page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(botSessionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create BotSession page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/bot-session/new$'));
        cy.getEntityCreateUpdateHeading('BotSession');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', botSessionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/bot-sessions',
          body: botSessionSample,
        }).then(({ body }) => {
          botSession = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/bot-sessions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/bot-sessions?page=0&size=20>; rel="last",<http://localhost/api/bot-sessions?page=0&size=20>; rel="first"',
              },
              body: [botSession],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(botSessionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details BotSession page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('botSession');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', botSessionPageUrlPattern);
      });

      it('edit button click should load edit BotSession page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BotSession');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', botSessionPageUrlPattern);
      });

      it('edit button click should load edit BotSession page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BotSession');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', botSessionPageUrlPattern);
      });

      it('last delete button click should delete instance of BotSession', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('botSession').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', botSessionPageUrlPattern);

        botSession = undefined;
      });
    });
  });

  describe('new BotSession page', () => {
    beforeEach(() => {
      cy.visit(`${botSessionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('BotSession');
    });

    it('should create an instance of BotSession', () => {
      cy.get(`[data-cy="waPhoneNumber"]`).type('what yogurt frilly');
      cy.get(`[data-cy="waPhoneNumber"]`).should('have.value', 'what yogurt frilly');

      cy.get(`[data-cy="currentState"]`).type('violently muted');
      cy.get(`[data-cy="currentState"]`).should('have.value', 'violently muted');

      cy.get(`[data-cy="sessionData"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="sessionData"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="lastActiveAt"]`).type('2025-12-14T14:53');
      cy.get(`[data-cy="lastActiveAt"]`).blur();
      cy.get(`[data-cy="lastActiveAt"]`).should('have.value', '2025-12-14T14:53');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        botSession = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', botSessionPageUrlPattern);
    });
  });
});
