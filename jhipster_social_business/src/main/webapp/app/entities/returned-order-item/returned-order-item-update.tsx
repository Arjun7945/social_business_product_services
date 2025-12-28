import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getFishProducts } from 'app/entities/fish-product/fish-product.reducer';
import { getEntities as getReturnedOrders } from 'app/entities/returned-order/returned-order.reducer';
import { createEntity, getEntity, reset, updateEntity } from './returned-order-item.reducer';

export const ReturnedOrderItemUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const fishProducts = useAppSelector(state => state.fishProduct.entities);
  const returnedOrders = useAppSelector(state => state.returnedOrder.entities);
  const returnedOrderItemEntity = useAppSelector(state => state.returnedOrderItem.entity);
  const loading = useAppSelector(state => state.returnedOrderItem.loading);
  const updating = useAppSelector(state => state.returnedOrderItem.updating);
  const updateSuccess = useAppSelector(state => state.returnedOrderItem.updateSuccess);

  const handleClose = () => {
    navigate(`/returned-order-item${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getFishProducts({}));
    dispatch(getReturnedOrders({}));
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
    if (values.quantity !== undefined && typeof values.quantity !== 'number') {
      values.quantity = Number(values.quantity);
    }

    const entity = {
      ...returnedOrderItemEntity,
      ...values,
      product: fishProducts.find(it => it.id.toString() === values.product?.toString()),
      returnedOrder: returnedOrders.find(it => it.id.toString() === values.returnedOrder?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...returnedOrderItemEntity,
          product: returnedOrderItemEntity?.product?.id,
          returnedOrder: returnedOrderItemEntity?.returnedOrder?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="whatsappProductServiceProApp.returnedOrderItem.home.createOrEditLabel" data-cy="ReturnedOrderItemCreateUpdateHeading">
            <Translate contentKey="whatsappProductServiceProApp.returnedOrderItem.home.createOrEditLabel">
              Create or edit a ReturnedOrderItem
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
                  id="returned-order-item-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('whatsappProductServiceProApp.returnedOrderItem.quantity')}
                id="returned-order-item-quantity"
                name="quantity"
                data-cy="quantity"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.returnedOrderItem.productComment')}
                id="returned-order-item-productComment"
                name="productComment"
                data-cy="productComment"
                type="text"
              />
              <ValidatedField
                id="returned-order-item-product"
                name="product"
                data-cy="product"
                label={translate('whatsappProductServiceProApp.returnedOrderItem.product')}
                type="select"
                required
              >
                <option value="" key="0" />
                {fishProducts
                  ? fishProducts.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="returned-order-item-returnedOrder"
                name="returnedOrder"
                data-cy="returnedOrder"
                label={translate('whatsappProductServiceProApp.returnedOrderItem.returnedOrder')}
                type="select"
                required
              >
                <option value="" key="0" />
                {returnedOrders
                  ? returnedOrders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/returned-order-item" replace color="info">
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

export default ReturnedOrderItemUpdate;
