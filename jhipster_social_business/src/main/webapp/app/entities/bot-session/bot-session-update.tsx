import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './bot-session.reducer';

export const BotSessionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const botSessionEntity = useAppSelector(state => state.botSession.entity);
  const loading = useAppSelector(state => state.botSession.loading);
  const updating = useAppSelector(state => state.botSession.updating);
  const updateSuccess = useAppSelector(state => state.botSession.updateSuccess);

  const handleClose = () => {
    navigate(`/bot-session${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.lastActiveAt = convertDateTimeToServer(values.lastActiveAt);

    const entity = {
      ...botSessionEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          lastActiveAt: displayDefaultDateTime(),
        }
      : {
          ...botSessionEntity,
          lastActiveAt: convertDateTimeFromServer(botSessionEntity.lastActiveAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="whatsappProductServiceProApp.botSession.home.createOrEditLabel" data-cy="BotSessionCreateUpdateHeading">
            <Translate contentKey="whatsappProductServiceProApp.botSession.home.createOrEditLabel">Create or edit a BotSession</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="bot-session-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('whatsappProductServiceProApp.botSession.waPhoneNumber')}
                id="bot-session-waPhoneNumber"
                name="waPhoneNumber"
                data-cy="waPhoneNumber"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.botSession.currentState')}
                id="bot-session-currentState"
                name="currentState"
                data-cy="currentState"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.botSession.sessionData')}
                id="bot-session-sessionData"
                name="sessionData"
                data-cy="sessionData"
                type="textarea"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.botSession.lastActiveAt')}
                id="bot-session-lastActiveAt"
                name="lastActiveAt"
                data-cy="lastActiveAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/bot-session" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default BotSessionUpdate;
