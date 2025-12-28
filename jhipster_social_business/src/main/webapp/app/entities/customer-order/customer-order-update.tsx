import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getOrderStatusHistories } from 'app/entities/order-status-history/order-status-history.reducer';
import { getEntities as getCustomers } from 'app/entities/customer/customer.reducer';
import { getEntities as getDeliveryPeople } from 'app/entities/delivery-person/delivery-person.reducer';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';
import { createEntity, getEntity, reset, updateEntity } from './customer-order.reducer';

export const CustomerOrderUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const orderStatusHistories = useAppSelector(state => state.orderStatusHistory.entities);
  const customers = useAppSelector(state => state.customer.entities);
  const deliveryPeople = useAppSelector(state => state.deliveryPerson.entities);
  const customerOrderEntity = useAppSelector(state => state.customerOrder.entity);
  const loading = useAppSelector(state => state.customerOrder.loading);
  const updating = useAppSelector(state => state.customerOrder.updating);
  const updateSuccess = useAppSelector(state => state.customerOrder.updateSuccess);
  const orderStatusValues = Object.keys(OrderStatus);

  const handleClose = () => {
    navigate(`/customer-order${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getOrderStatusHistories({}));
    dispatch(getCustomers({}));
    dispatch(getDeliveryPeople({}));
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
    values.orderTime = convertDateTimeToServer(values.orderTime);
    if (values.totalAmount !== undefined && typeof values.totalAmount !== 'number') {
      values.totalAmount = Number(values.totalAmount);
    }
    values.confirmedAt = convertDateTimeToServer(values.confirmedAt);
    if (values.removedCustomerId !== undefined && typeof values.removedCustomerId !== 'number') {
      values.removedCustomerId = Number(values.removedCustomerId);
    }
    if (values.removedDeliveryPersonId !== undefined && typeof values.removedDeliveryPersonId !== 'number') {
      values.removedDeliveryPersonId = Number(values.removedDeliveryPersonId);
    }

    const entity = {
      ...customerOrderEntity,
      ...values,
      history: orderStatusHistories.find(it => it.id.toString() === values.history?.toString()),
      customer: customers.find(it => it.id.toString() === values.customer?.toString()),
      deliveryPerson: deliveryPeople.find(it => it.id.toString() === values.deliveryPerson?.toString()),
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
          orderTime: displayDefaultDateTime(),
          confirmedAt: displayDefaultDateTime(),
        }
      : {
          status: 'ORDER_NOT_TAKEN',
          ...customerOrderEntity,
          orderTime: convertDateTimeFromServer(customerOrderEntity.orderTime),
          confirmedAt: convertDateTimeFromServer(customerOrderEntity.confirmedAt),
          history: customerOrderEntity?.history?.id,
          customer: customerOrderEntity?.customer?.id,
          deliveryPerson: customerOrderEntity?.deliveryPerson?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="whatsappProductServiceProApp.customerOrder.home.createOrEditLabel" data-cy="CustomerOrderCreateUpdateHeading">
            <Translate contentKey="whatsappProductServiceProApp.customerOrder.home.createOrEditLabel">
              Create or edit a CustomerOrder
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
                  id="customer-order-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customerOrder.orderTime')}
                id="customer-order-orderTime"
                name="orderTime"
                data-cy="orderTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customerOrder.totalAmount')}
                id="customer-order-totalAmount"
                name="totalAmount"
                data-cy="totalAmount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customerOrder.status')}
                id="customer-order-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {orderStatusValues.map(orderStatus => (
                  <option value={orderStatus} key={orderStatus}>
                    {translate(`whatsappProductServiceProApp.OrderStatus.${orderStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">
                <Translate contentKey="whatsappProductServiceProApp.customerOrder.help.status" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customerOrder.paymentMethod')}
                id="customer-order-paymentMethod"
                name="paymentMethod"
                data-cy="paymentMethod"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customerOrder.confirmedAt')}
                id="customer-order-confirmedAt"
                name="confirmedAt"
                data-cy="confirmedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customerOrder.removedCustomerId')}
                id="customer-order-removedCustomerId"
                name="removedCustomerId"
                data-cy="removedCustomerId"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customerOrder.removedDeliveryPersonId')}
                id="customer-order-removedDeliveryPersonId"
                name="removedDeliveryPersonId"
                data-cy="removedDeliveryPersonId"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customerOrder.transactionId')}
                id="customer-order-transactionId"
                name="transactionId"
                data-cy="transactionId"
                type="text"
              />
              <ValidatedField
                id="customer-order-history"
                name="history"
                data-cy="history"
                label={translate('whatsappProductServiceProApp.customerOrder.history')}
                type="select"
              >
                <option value="" key="0" />
                {orderStatusHistories
                  ? orderStatusHistories.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="customer-order-customer"
                name="customer"
                data-cy="customer"
                label={translate('whatsappProductServiceProApp.customerOrder.customer')}
                type="select"
              >
                <option value="" key="0" />
                {customers
                  ? customers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="customer-order-deliveryPerson"
                name="deliveryPerson"
                data-cy="deliveryPerson"
                label={translate('whatsappProductServiceProApp.customerOrder.deliveryPerson')}
                type="select"
              >
                <option value="" key="0" />
                {deliveryPeople
                  ? deliveryPeople.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/customer-order" replace color="info">
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

export default CustomerOrderUpdate;
