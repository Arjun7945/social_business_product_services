import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './button-action.reducer';

export const ButtonActionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const buttonActionEntity = useAppSelector(state => state.buttonAction.entity);
  const loading = useAppSelector(state => state.buttonAction.loading);
  const updating = useAppSelector(state => state.buttonAction.updating);
  const updateSuccess = useAppSelector(state => state.buttonAction.updateSuccess);

  const handleClose = () => {
    navigate(`/button-action${location.search}`);
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
    values.clickedAt = convertDateTimeToServer(values.clickedAt);

    const entity = {
      ...buttonActionEntity,
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
          clickedAt: displayDefaultDateTime(),
        }
      : {
          ...buttonActionEntity,
          clickedAt: convertDateTimeFromServer(buttonActionEntity.clickedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="whatsappProductServiceProApp.buttonAction.home.createOrEditLabel" data-cy="ButtonActionCreateUpdateHeading">
            <Translate contentKey="whatsappProductServiceProApp.buttonAction.home.createOrEditLabel">
              Create or edit a ButtonAction
            </Translate>
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
                  id="button-action-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('whatsappProductServiceProApp.buttonAction.waMessageId')}
                id="button-action-waMessageId"
                name="waMessageId"
                data-cy="waMessageId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.buttonAction.buttonId')}
                id="button-action-buttonId"
                name="buttonId"
                data-cy="buttonId"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.buttonAction.clickedAt')}
                id="button-action-clickedAt"
                name="clickedAt"
                data-cy="clickedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.buttonAction.clickedBy')}
                id="button-action-clickedBy"
                name="clickedBy"
                data-cy="clickedBy"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/button-action" replace color="info">
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

export default ButtonActionUpdate;
