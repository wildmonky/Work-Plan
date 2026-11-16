import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_b6593654eaad18642ca02c44c58d4fef
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.getMapValueByKey("馈线新投;馈线改名;馈线退运;",mapRow.get("CLLX"));

    }
}
