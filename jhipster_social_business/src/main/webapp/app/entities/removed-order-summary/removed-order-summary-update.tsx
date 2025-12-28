import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { UserRole } from 'app/shared/model/enumerations/user-role.model';
import { createEntity, getEntity, reset, updateEntity } from './removed-order-summary.reducer';

export const RemovedOrderSummaryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const removedOrderSummaryEntity = useAppSelector(state => state.removedOrderSummary.entity);
  const loading = useAppSelector(state => state.removedOrderSummary.loading);
  const updating = useAppSelector(state => state.removedOrderSummary.updating);
  const updateSuccess = useAppSelector(state => state.removedOrderSummary.updateSuccess);
  const userRoleValues = Object.keys(UserRole);

  const handleClose = () => {
    navigate(`/removed-order-summary${location.search}`);
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
    if (values.userOriginalId !== undefined && typeof values.userOriginalId !== 'number') {
      values.userOriginalId = Number(values.userOriginalId);
    }
    if (values.totalOrders !== undefined && typeof values.totalOrders !== 'number') {
      values.totalOrders = Number(values.totalOrders);
    }
    if (values.totalAmount !== undefined && typeof values.totalAmount !== 'number') {
      values.totalAmount = Number(values.totalAmount);
    }
    values.firstInteractionAt = convertDateTimeToServer(values.firstInteractionAt);
    values.lastInteractionAt = convertDateTimeToServer(values.lastInteractionAt);
    values.removedAt = convertDateTimeToServer(values.removedAt);

    const entity = {
      ...removedOrderSummaryEntity,
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
          firstInteractionAt: displayDefaultDateTime(),
          lastInteractionAt: displayDefaultDateTime(),
          removedAt: displayDefaultDateTime(),
        }
      : {
          userRole: 'CUSTOMER',
          ...removedOrderSummaryEntity,
          firstInteractionAt: convertDateTimeFromServer(removedOrderSummaryEntity.firstInteractionAt),
          lastInteractionAt: convertDateTimeFromServer(removedOrderSummaryEntity.lastInteractionAt),
          removedAt: convertDateTimeFromServer(removedOrderSummaryEntity.removedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="whatsappProductServiceProApp.removedOrderSummary.home.createOrEditLabel" data-cy="RemovedOrderSummaryCreateUpdateHeading">
            <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.home.createOrEditLabel">
              Create or edit a RemovedOrderSummary
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
                  id="removed-order-summary-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedOrderSummary.userOriginalId')}
                id="removed-order-summary-userOriginalId"
                name="userOriginalId"
                data-cy="userOriginalId"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedOrderSummary.userName')}
                id="removed-order-summary-userName"
                name="userName"
                data-cy="userName"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedOrderSummary.userRole')}
                id="removed-order-summary-userRole"
                name="userRole"
                data-cy="userRole"
                type="select"
              >
                {userRoleValues.map(userRole => (
                  <option value={userRole} key={userRole}>
                    {translate(`whatsappProductServiceProApp.UserRole.${userRole}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedOrderSummary.totalOrders')}
                id="removed-order-summary-totalOrders"
                name="totalOrders"
                data-cy="totalOrders"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedOrderSummary.totalAmount')}
                id="removed-order-summary-totalAmount"
                name="totalAmount"
                data-cy="totalAmount"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedOrderSummary.firstInteractionAt')}
                id="removed-order-summary-firstInteractionAt"
                name="firstInteractionAt"
                data-cy="firstInteractionAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedOrderSummary.lastInteractionAt')}
                id="removed-order-summary-lastInteractionAt"
                name="lastInteractionAt"
                data-cy="lastInteractionAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedOrderSummary.removedAt')}
                id="removed-order-summary-removedAt"
                name="removedAt"
                data-cy="removedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/removed-order-summary" replace color="info">
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

export default RemovedOrderSummaryUpdate;
