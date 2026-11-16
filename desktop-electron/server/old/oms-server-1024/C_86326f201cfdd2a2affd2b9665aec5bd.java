import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_86326f201cfdd2a2affd2b9665aec5bd
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.getMsgByKey("10kV;110kV;220kV;500kV",mapRow.get("F_DYDJ"));

    }
}
